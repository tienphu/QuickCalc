package com.quickcalcsheet.app.ui

import com.quickcalcsheet.app.data.local.SheetEntity

enum class ColumnTarget {
    COL_1,
    COL_2
}

data class GridCursor(
    val rowIndex: Int = 0,
    val column: ColumnTarget = ColumnTarget.COL_1
)

data class SheetRowUi(
    val id: Long,
    val rowNumber: Int,
    val col1Raw: String = "",
    val col2Raw: String = "",
    val col3Formatted: String = ""
)

data class SpreadsheetUiState(
    val activeSheetId: Long = 0L,
    val sheetTitle: String = "Untitled Draft",
    val isDraft: Boolean = true,
    val createdAt: Long = 0L,
    val rows: List<SheetRowUi> = emptyList(),
    val cursor: GridCursor = GridCursor(rowIndex = 0, column = ColumnTarget.COL_1),
    val totalCol1: String = "0",
    val totalCol3: String = "0",
    val savedSheets: List<SheetEntity> = emptyList(),
    val isSaveDialogVisible: Boolean = false,
    val isSavedListVisible: Boolean = false,
    val isPwaGuideVisible: Boolean = false,
    val toastMessage: String? = null
)
