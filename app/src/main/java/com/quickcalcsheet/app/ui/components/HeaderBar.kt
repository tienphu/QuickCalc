package com.quickcalcsheet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickcalcsheet.app.ui.theme.EmeraldAccent
import com.quickcalcsheet.app.ui.theme.SlateBorder
import com.quickcalcsheet.app.ui.theme.SlateSurface
import com.quickcalcsheet.app.ui.theme.SlateSurfaceMuted
import com.quickcalcsheet.app.ui.theme.SlateTextPrimary
import com.quickcalcsheet.app.ui.theme.SlateTextSecondary

@Composable
fun HeaderBar(
    sheetTitle: String,
    isDraft: Boolean,
    onNewClick: () -> Unit,
    onSaveClick: () -> Unit,
    onSavedClick: () -> Unit,
    onPwaGuideClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "QuickCalc Sheet",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isDraft) "Auto-saved · $sheetTitle" else sheetTitle,
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeaderActionButton(
                    label = "New",
                    icon = Icons.Outlined.Add,
                    onClick = onNewClick
                )
                HeaderActionButton(
                    label = "Save",
                    icon = Icons.Outlined.Save,
                    isPrimary = true,
                    onClick = onSaveClick
                )
                HeaderActionButton(
                    label = "Saved",
                    icon = Icons.Outlined.FolderOpen,
                    onClick = onSavedClick
                )
                TextButton(
                    onClick = onPwaGuideClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = SlateTextSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "PWA Guide",
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "PWA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
        HorizontalDivider(color = SlateBorder, thickness = 1.dp)
    }
}

@Composable
private fun HeaderActionButton(
    label: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isPrimary) EmeraldAccent else SlateSurfaceMuted
    val fgColor = if (isPrimary) Color.White else SlateTextPrimary
    val borderColor = if (isPrimary) EmeraldAccent else SlateBorder

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = fgColor,
            modifier = Modifier.height(15.dp)
        )
        Text(
            text = label,
            color = fgColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
