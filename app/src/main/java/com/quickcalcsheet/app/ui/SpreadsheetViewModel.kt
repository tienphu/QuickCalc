package com.quickcalcsheet.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quickcalcsheet.app.data.local.AppDatabase
import com.quickcalcsheet.app.data.local.SessionPrefs
import com.quickcalcsheet.app.data.local.SheetEntity
import com.quickcalcsheet.app.data.local.SheetRowCodec
import com.quickcalcsheet.app.data.local.SheetRowData
import com.quickcalcsheet.app.data.repository.SheetRepository
import com.quickcalcsheet.app.domain.DecimalCalculator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SpreadsheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SheetRepository
    private val _uiState = MutableStateFlow(SpreadsheetUiState())
    val uiState: StateFlow<SpreadsheetUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null

    init {
        val db = AppDatabase.getInstance(application)
        val prefs = SessionPrefs(application)
        repository = SheetRepository(db.sheetDao(), prefs)

        viewModelScope.launch {
            repository.observeSavedSheets().collect { sheets ->
                _uiState.update { it.copy(savedSheets = sheets) }
            }
        }

        viewModelScope.launch {
            val active = repository.loadOrCreateActiveSheet()
            val (savedRow, savedColName) = repository.getSavedCursor()
            val savedCol = runCatching { ColumnTarget.valueOf(savedColName) }
                .getOrDefault(ColumnTarget.COL_1)
            applySheetToState(active, GridCursor(savedRow, savedCol))
        }
    }

    fun selectCell(rowIndex: Int, column: ColumnTarget) {
        val rows = _uiState.value.rows
        if (rowIndex !in rows.indices) return
        val nextCursor = GridCursor(rowIndex = rowIndex, column = column)
        _uiState.update { it.copy(cursor = nextCursor) }
        repository.persistCursor(nextCursor.rowIndex, nextCursor.column.name)
    }

    fun onKeyDigit(digit: Char) {
        if (digit !in '0'..'9') return
        updateActiveCellText { current ->
            if (current == "0") digit.toString() else current + digit
        }
    }

    fun onKeyDecimal(separator: Char) {
        if (separator != '.' && separator != ',') return
        updateActiveCellText { current ->
            // Prevent duplicate decimal separators in a single number
            if (current.contains('.') || current.contains(',')) {
                current
            } else if (current.isEmpty()) {
                "0$separator"
            } else {
                current + separator
            }
        }
    }

    fun onKeyBackspace() {
        updateActiveCellText { current ->
            if (current.isNotEmpty()) current.dropLast(1) else current
        }
    }

    /**
     * Top-to-Bottom, Col 1 -> Col 2 navigation:
     * Visual order: [No.] [Col 1] [Col 2] [Col 3]
     * Cursor starts at Row 1, Col 1 -> Enter moves right to Row 1, Col 2
     * -> Enter moves to Row 2, Col 1 -> appends a new row when at the bottom.
     */
    fun onKeyEnter() {
        val state = _uiState.value
        val currentRow = state.cursor.rowIndex
        val currentCol = state.cursor.column

        if (currentCol == ColumnTarget.COL_1) {
            val nextCursor = GridCursor(rowIndex = currentRow, column = ColumnTarget.COL_2)
            _uiState.update { it.copy(cursor = nextCursor) }
            repository.persistCursor(nextCursor.rowIndex, nextCursor.column.name)
        } else {
            val nextRowIndex = currentRow + 1
            val currentRows = state.rows
            val updatedRows = if (nextRowIndex >= currentRows.size) {
                val nextId = (currentRows.maxOfOrNull { it.id } ?: 0L) + 1L
                currentRows + SheetRowUi(
                    id = nextId,
                    rowNumber = currentRows.size + 1
                )
            } else {
                currentRows
            }
            val nextCursor = GridCursor(rowIndex = nextRowIndex, column = ColumnTarget.COL_1)
            _uiState.update {
                it.copy(
                    rows = updatedRows,
                    cursor = nextCursor
                )
            }
            repository.persistCursor(nextCursor.rowIndex, nextCursor.column.name)
            scheduleAutoSave()
        }
    }

    fun createNewSheet() {
        viewModelScope.launch {
            val fresh = repository.createFreshDraftSheet()
            applySheetToState(fresh, GridCursor(0, ColumnTarget.COL_1))
            showToast("Created new sheet")
        }
    }

    fun openSaveDialog() {
        _uiState.update { it.copy(isSaveDialogVisible = true) }
    }

    fun dismissSaveDialog() {
        _uiState.update { it.copy(isSaveDialogVisible = false) }
    }

    fun buildFormattedTitleWithDate(rawTitle: String): String {
        val dateSuffix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) // YYYYMMDD
        val clean = rawTitle.trim()
            .ifEmpty { "my_sheet" }
            .replace(Regex("\\s+"), "_")
        val suffixToken = "_$dateSuffix"
        return if (clean.endsWith(suffixToken)) clean else "$clean$suffixToken"
    }

    fun saveSheetWithTitle(rawTitle: String) {
        val state = _uiState.value
        if (state.activeSheetId == 0L) return
        val formattedTitle = buildFormattedTitleWithDate(rawTitle)

        viewModelScope.launch {
            val saved = repository.saveNamedSheet(
                existingSheetId = state.activeSheetId,
                isCurrentlyDraft = state.isDraft,
                formattedTitle = formattedTitle,
                createdAt = state.createdAt,
                rows = state.rows.toDataRows()
            )
            _uiState.update {
                it.copy(
                    activeSheetId = saved.id,
                    sheetTitle = saved.title,
                    isDraft = false,
                    createdAt = saved.createdAt,
                    isSaveDialogVisible = false
                )
            }
            showToast("Saved as $formattedTitle")
        }
    }

    fun openSavedList() {
        _uiState.update { it.copy(isSavedListVisible = true) }
    }

    fun dismissSavedList() {
        _uiState.update { it.copy(isSavedListVisible = false) }
    }

    fun openSavedSheet(sheetId: Long) {
        viewModelScope.launch {
            val sheet = repository.openSheet(sheetId) ?: return@launch
            applySheetToState(sheet, GridCursor(0, ColumnTarget.COL_1))
            _uiState.update { it.copy(isSavedListVisible = false) }
            showToast("Loaded ${sheet.title}")
        }
    }

    fun deleteSavedSheet(sheetId: Long) {
        viewModelScope.launch {
            val wasActive = _uiState.value.activeSheetId == sheetId
            repository.deleteSheet(sheetId)
            if (wasActive) {
                val fresh = repository.createFreshDraftSheet()
                applySheetToState(fresh, GridCursor(0, ColumnTarget.COL_1))
            }
            showToast("Sheet deleted")
        }
    }

    fun openPwaGuide() {
        _uiState.update { it.copy(isPwaGuideVisible = true) }
    }

    fun dismissPwaGuide() {
        _uiState.update { it.copy(isPwaGuideVisible = false) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun autoSaveNow() {
        val state = _uiState.value
        if (state.activeSheetId == 0L || state.rows.isEmpty()) return
        viewModelScope.launch {
            repository.autoSaveRows(
                sheetId = state.activeSheetId,
                currentTitle = state.sheetTitle,
                isDraft = state.isDraft,
                createdAt = state.createdAt,
                rows = state.rows.toDataRows()
            )
        }
    }

    private fun updateActiveCellText(transform: (String) -> String) {
        val state = _uiState.value
        val rowIdx = state.cursor.rowIndex
        if (rowIdx !in state.rows.indices) return

        val updatedRows = state.rows.mapIndexed { index, row ->
            if (index != rowIdx) {
                row
            } else {
                val newCol1 = if (state.cursor.column == ColumnTarget.COL_1) transform(row.col1Raw) else row.col1Raw
                val newCol2 = if (state.cursor.column == ColumnTarget.COL_2) transform(row.col2Raw) else row.col2Raw
                val product = DecimalCalculator.multiply(newCol1, newCol2)
                row.copy(
                    col1Raw = newCol1,
                    col2Raw = newCol2,
                    col3Formatted = DecimalCalculator.formatResult(product)
                )
            }
        }

        val (total1, total3) = computeTotals(updatedRows)
        _uiState.update {
            it.copy(
                rows = updatedRows,
                totalCol1 = total1,
                totalCol3 = total3
            )
        }
        scheduleAutoSave()
    }

    private fun scheduleAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            val state = _uiState.value
            if (state.activeSheetId == 0L) return@launch
            repository.autoSaveRows(
                sheetId = state.activeSheetId,
                currentTitle = state.sheetTitle,
                isDraft = state.isDraft,
                createdAt = state.createdAt,
                rows = state.rows.toDataRows()
            )
        }
    }

    private fun applySheetToState(entity: SheetEntity, cursor: GridCursor) {
        val decoded = SheetRowCodec.decode(entity.rowsJson).ifEmpty {
            List(10) { idx -> SheetRowData(id = (idx + 1).toLong()) }
        }
        val uiRows = decoded.mapIndexed { idx, row ->
            val product = DecimalCalculator.multiply(row.col1, row.col2)
            SheetRowUi(
                id = row.id,
                rowNumber = idx + 1,
                col1Raw = row.col1,
                col2Raw = row.col2,
                col3Formatted = DecimalCalculator.formatResult(product)
            )
        }
        val safeRowIndex = cursor.rowIndex.coerceIn(0, uiRows.lastIndex)
        val (total1, total3) = computeTotals(uiRows)
        _uiState.update {
            it.copy(
                activeSheetId = entity.id,
                sheetTitle = entity.title,
                isDraft = entity.isDraft,
                createdAt = entity.createdAt,
                rows = uiRows,
                cursor = cursor.copy(rowIndex = safeRowIndex),
                totalCol1 = total1,
                totalCol3 = total3
            )
        }
    }

    private fun computeTotals(rows: List<SheetRowUi>): Pair<String, String> {
        val sumCol1 = DecimalCalculator.sumRaw(rows.map { it.col1Raw })
        val sumCol3 = DecimalCalculator.sumDecimals(
            rows.map { DecimalCalculator.multiply(it.col1Raw, it.col2Raw) }
        )
        return DecimalCalculator.formatResult(sumCol1) to DecimalCalculator.formatResult(sumCol3)
    }

    private fun List<SheetRowUi>.toDataRows(): List<SheetRowData> =
        map { SheetRowData(id = it.id, col1 = it.col1Raw, col2 = it.col2Raw) }

    private fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }
}
