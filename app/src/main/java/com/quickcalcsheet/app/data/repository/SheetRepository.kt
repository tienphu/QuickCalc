package com.quickcalcsheet.app.data.repository

import com.quickcalcsheet.app.data.local.SessionPrefs
import com.quickcalcsheet.app.data.local.SheetDao
import com.quickcalcsheet.app.data.local.SheetEntity
import com.quickcalcsheet.app.data.local.SheetRowCodec
import com.quickcalcsheet.app.data.local.SheetRowData
import com.quickcalcsheet.app.domain.DecimalCalculator
import kotlinx.coroutines.flow.Flow

class SheetRepository(
    private val sheetDao: SheetDao,
    private val sessionPrefs: SessionPrefs
) {
    fun observeSavedSheets(): Flow<List<SheetEntity>> = sheetDao.observeSavedSheets()

    suspend fun loadOrCreateActiveSheet(): SheetEntity {
        val activeId = sessionPrefs.activeSheetId
        if (activeId != SessionPrefs.NO_SHEET_ID) {
            sheetDao.getById(activeId)?.let { return it }
        }
        sheetDao.getLatestDraft()?.let { draft ->
            sessionPrefs.activeSheetId = draft.id
            return draft
        }
        return createFreshDraftSheet()
    }

    suspend fun createFreshDraftSheet(initialRowCount: Int = 10): SheetEntity {
        val now = System.currentTimeMillis()
        val blankRows = List(initialRowCount) { index ->
            SheetRowData(id = (index + 1).toLong())
        }
        val entity = SheetEntity(
            title = "Untitled Draft",
            isDraft = true,
            rowsJson = SheetRowCodec.encode(blankRows),
            rowCount = blankRows.size,
            totalCol1 = "0",
            totalCol3 = "0",
            createdAt = now,
            updatedAt = now
        )
        val newId = sheetDao.insert(entity)
        sessionPrefs.activeSheetId = newId
        sessionPrefs.saveCursor(0, "COL_1")
        return entity.copy(id = newId)
    }

    suspend fun autoSaveRows(
        sheetId: Long,
        currentTitle: String,
        isDraft: Boolean,
        createdAt: Long,
        rows: List<SheetRowData>
    ): SheetEntity {
        val total1 = DecimalCalculator.formatResult(DecimalCalculator.sumRaw(rows.map { it.col1 }))
        val total3 = DecimalCalculator.formatResult(
            DecimalCalculator.sumDecimals(rows.map { DecimalCalculator.multiply(it.col1, it.col2) })
        )
        val updated = SheetEntity(
            id = sheetId,
            title = currentTitle,
            isDraft = isDraft,
            rowsJson = SheetRowCodec.encode(rows),
            rowCount = rows.count { it.col1.isNotBlank() || it.col2.isNotBlank() }.coerceAtLeast(1),
            totalCol1 = total1,
            totalCol3 = total3,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis()
        )
        sheetDao.update(updated)
        return updated
    }

    suspend fun saveNamedSheet(
        existingSheetId: Long,
        isCurrentlyDraft: Boolean,
        formattedTitle: String,
        createdAt: Long,
        rows: List<SheetRowData>
    ): SheetEntity {
        val now = System.currentTimeMillis()
        val total1 = DecimalCalculator.formatResult(DecimalCalculator.sumRaw(rows.map { it.col1 }))
        val total3 = DecimalCalculator.formatResult(
            DecimalCalculator.sumDecimals(rows.map { DecimalCalculator.multiply(it.col1, it.col2) })
        )
        val entity = SheetEntity(
            id = if (isCurrentlyDraft) existingSheetId else 0L,
            title = formattedTitle,
            isDraft = false,
            rowsJson = SheetRowCodec.encode(rows),
            rowCount = rows.count { it.col1.isNotBlank() || it.col2.isNotBlank() }.coerceAtLeast(1),
            totalCol1 = total1,
            totalCol3 = total3,
            createdAt = if (isCurrentlyDraft) createdAt else now,
            updatedAt = now
        )
        val savedId = if (isCurrentlyDraft) {
            sheetDao.update(entity)
            existingSheetId
        } else {
            sheetDao.insert(entity)
        }
        sessionPrefs.activeSheetId = savedId
        return entity.copy(id = savedId)
    }

    suspend fun openSheet(sheetId: Long): SheetEntity? {
        val sheet = sheetDao.getById(sheetId) ?: return null
        sessionPrefs.activeSheetId = sheet.id
        sessionPrefs.saveCursor(0, "COL_1")
        return sheet
    }

    suspend fun deleteSheet(sheetId: Long) {
        sheetDao.deleteById(sheetId)
        if (sessionPrefs.activeSheetId == sheetId) {
            sessionPrefs.activeSheetId = SessionPrefs.NO_SHEET_ID
        }
    }

    fun getSavedCursor(): Pair<Int, String> {
        return sessionPrefs.cursorRow to sessionPrefs.cursorColumn
    }

    fun persistCursor(row: Int, column: String) {
        sessionPrefs.saveCursor(row, column)
    }
}
