package com.quickcalcsheet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickcalcsheet.app.ui.theme.SummaryBg

/**
 * Fixed Summary Row directly above the custom numeric keypad.
 * Displays cumulative totals ONLY for Col 1 and Col 3 (Col 2 intentionally left blank).
 */
@Composable
fun SummaryRow(
    totalCol1: String,
    totalCol3: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(SummaryBg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Total label under No.
        Box(
            modifier = Modifier
                .weight(WEIGHT_NO)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Σ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )
        }

        // Cumulative Total for Col 1
        Box(
            modifier = Modifier
                .weight(WEIGHT_COL1)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = totalCol1,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Col 2 intentionally left blank per specification
        Box(modifier = Modifier.weight(WEIGHT_COL2))

        // Cumulative Total for Col 3
        Box(
            modifier = Modifier
                .weight(WEIGHT_COL3)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = totalCol3,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5EEAD4),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
