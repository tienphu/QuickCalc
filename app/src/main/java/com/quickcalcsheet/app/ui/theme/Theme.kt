package com.quickcalcsheet.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SlateCanvas = Color(0xFFF8FAFC)
val SlateSurface = Color(0xFFFFFFFF)
val SlateSurfaceMuted = Color(0xFFF1F5F9)
val SlateBorder = Color(0xFFE2E8F0)
val SlateTextPrimary = Color(0xFF0F172A)
val SlateTextSecondary = Color(0xFF475569)
val EmeraldAccent = Color(0xFF0F766E)
val EmeraldTint = Color(0xFFCCFBF1)
val ActiveCellBorder = Color(0xFF0284C7)
val ActiveCellBg = Color(0xFFE0F2FE)
val SummaryBg = Color(0xFF0F172A)
val KeypadBg = Color(0xFFE2E8F0)

private val QuickCalcLightScheme = lightColorScheme(
    primary = EmeraldAccent,
    onPrimary = Color.White,
    secondary = ActiveCellBorder,
    background = SlateCanvas,
    surface = SlateSurface,
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    outline = SlateBorder
)

@Composable
fun QuickCalcTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = QuickCalcLightScheme,
        content = content
    )
}
