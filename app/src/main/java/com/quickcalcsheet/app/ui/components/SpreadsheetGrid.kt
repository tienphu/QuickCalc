package com.quickcalcsheet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickcalcsheet.app.ui.ColumnTarget
import com.quickcalcsheet.app.ui.GridCursor
import com.quickcalcsheet.app.ui.SheetRowUi
import com.quickcalcsheet.app.ui.theme.ActiveCellBg
import com.quickcalcsheet.app.ui.theme.ActiveCellBorder
import com.quickcalcsheet.app.ui.theme.EmeraldAccent
import com.quickcalcsheet.app.ui.theme.SlateBorder
import com.quickcalcsheet.app.ui.theme.SlateCanvas
import com.quickcalcsheet.app.ui.theme.SlateSurface
import com.quickcalcsheet.app.ui.theme.SlateSurfaceMuted
import com.quickcalcsheet.app.ui.theme.SlateTextPrimary
import com.quickcalcsheet.app.ui.theme.SlateTextSecondary

/**
 * Column weights shared with SummaryRow to guarantee vertical alignment:
 * [No.] (0.45f) | [Col 1] (1f) | [Col 2] (1f) | [Col 3 = C1*C2] (1.15f)
 * Entry starts at Row 1, Col 1 (leftmost input), moves right to Col 2, then down.
 */
internal const val WEIGHT_NO = 0.45f
internal const val WEIGHT_COL1 = 1.0f
internal const val WEIGHT_COL2 = 1.0f
internal const val WEIGHT_COL3 = 1.15f

@Composable
fun SpreadsheetGrid(
    rows: List<SheetRowUi>,
    cursor: GridCursor,
    onCellClick: (rowIndex: Int, column: ColumnTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(cursor.rowIndex, rows.size) {
        if (cursor.rowIndex in rows.indices) {
            listState.animateScrollToItem(cursor.rowIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateCanvas)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateSurfaceMuted)
                .height(38.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GridHeaderCell(
                text = "No.",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(WEIGHT_NO)
            )
            GridHeaderCell(
                text = "Col 1",
                textAlign = TextAlign.End,
                isPrimaryInput = true,
                modifier = Modifier.weight(WEIGHT_COL1)
            )
            GridHeaderCell(
                text = "Col 2",
                textAlign = TextAlign.End,
                modifier = Modifier.weight(WEIGHT_COL2)
            )
            GridHeaderCell(
                text = "Col 3 (C1×C2)",
                textAlign = TextAlign.End,
                modifier = Modifier.weight(WEIGHT_COL3)
            )
        }
        HorizontalDivider(color = SlateBorder, thickness = 1.dp)

        // Vertically Scrollable Rows
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = rows,
                key = { _, item -> item.id }
            ) { index, row ->
                val isRowActive = cursor.rowIndex == index
                val isCol1Active = isRowActive && cursor.column == ColumnTarget.COL_1
                val isCol2Active = isRowActive && cursor.column == ColumnTarget.COL_2

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(if (index % 2 == 0) SlateSurface else Color(0xFFFAFBFD)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // STT / Row No.
                    Box(
                        modifier = Modifier
                            .weight(WEIGHT_NO)
                            .height(44.dp)
                            .border(0.5.dp, SlateBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = row.rowNumber.toString(),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isRowActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRowActive) ActiveCellBorder else SlateTextSecondary
                        )
                    }

                    // Col 1 (Editable via custom keypad, blocked system keyboard)
                    EditableNumericCell(
                        value = row.col1Raw,
                        isSelected = isCol1Active,
                        onClick = { onCellClick(index, ColumnTarget.COL_1) },
                        modifier = Modifier.weight(WEIGHT_COL1)
                    )

                    // Col 2 (Editable via custom keypad, blocked system keyboard)
                    EditableNumericCell(
                        value = row.col2Raw,
                        isSelected = isCol2Active,
                        onClick = { onCellClick(index, ColumnTarget.COL_2) },
                        modifier = Modifier.weight(WEIGHT_COL2)
                    )

                    // Col 3 (Auto-calculated = Col 1 * Col 2, read-only)
                    Box(
                        modifier = Modifier
                            .weight(WEIGHT_COL3)
                            .height(44.dp)
                            .background(Color(0xFFF4F7FA))
                            .border(0.5.dp, SlateBorder)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = row.col3Formatted,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridHeaderCell(
    text: String,
    textAlign: TextAlign,
    isPrimaryInput: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .border(0.5.dp, SlateBorder)
            .padding(horizontal = 8.dp),
        contentAlignment = if (textAlign == TextAlign.Center) Alignment.Center else Alignment.CenterEnd
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPrimaryInput) EmeraldAccent else SlateTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EditableNumericCell(
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(if (isSelected) ActiveCellBg else Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) ActiveCellBorder else SlateBorder
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = value.ifEmpty { if (isSelected) "|" else "" },
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (value.isEmpty() && isSelected) ActiveCellBorder else SlateTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
