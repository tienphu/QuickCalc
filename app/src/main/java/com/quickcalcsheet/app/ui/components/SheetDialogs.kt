package com.quickcalcsheet.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickcalcsheet.app.data.local.SheetEntity
import com.quickcalcsheet.app.ui.theme.EmeraldAccent
import com.quickcalcsheet.app.ui.theme.SlateBorder
import com.quickcalcsheet.app.ui.theme.SlateTextPrimary
import com.quickcalcsheet.app.ui.theme.SlateTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SaveSheetDialog(
    initialTitle: String,
    previewFormatter: (String) -> String,
    onDismiss: () -> Unit,
    onConfirmSave: (String) -> Unit
) {
    var titleInput by remember {
        mutableStateOf(
            if (initialTitle == "Untitled Draft") "my_sheet" else initialTitle
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Save Spreadsheet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Enter a sheet title. Today's date suffix (_YYYYMMDD) is appended automatically.",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Sheet Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "File name: ${previewFormatter(titleInput)}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = EmeraldAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmSave(titleInput) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SavedSheetsDialog(
    sheets: List<SheetEntity>,
    onDismiss: () -> Unit,
    onOpenSheet: (Long) -> Unit,
    onDeleteSheet: (Long) -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.US)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Saved Spreadsheets",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            if (sheets.isEmpty()) {
                Text(
                    text = "No saved sheets yet. Tap Save in the header to store a named spreadsheet.",
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sheets, key = { it.id }) { sheet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                                .clickable { onOpenSheet(sheet.id) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sheet.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = "${sheet.rowCount} rows · Total Col 3: ${sheet.totalCol3}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = EmeraldAccent
                                )
                                Text(
                                    text = dateFormatter.format(Date(sheet.updatedAt)),
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }
                            IconButton(onClick = { onDeleteSheet(sheet.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "Delete sheet",
                                    tint = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PwaGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "QuickCalc & PWA Guide",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideSection(
                    title = "1. Spreadsheet Entry Flow",
                    body = "Cursor starts at Row 1, Col 1 (left). Enter moves to Col 2, computes Col 3 = Col 1 × Col 2 automatically on the right, then steps down to the next row."
                )
                HorizontalDivider(color = SlateBorder)
                GuideSection(
                    title = "2. Auto-Row & Summary",
                    body = "Pressing Enter on Col 2 of the last row creates a new row automatically. The pinned summary bar totals Col 1 and Col 3."
                )
                HorizontalDivider(color = SlateBorder)
                GuideSection(
                    title = "3. Companion Web PWA Install",
                    body = "When opening QuickCalc Sheet in Chrome or Safari on mobile, tap Browser Menu -> Add to Home Screen (Install App) for offline full-screen use."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun GuideSection(title: String, body: String) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = body,
            fontSize = 12.sp,
            color = SlateTextSecondary,
            lineHeight = 17.sp
        )
    }
}
