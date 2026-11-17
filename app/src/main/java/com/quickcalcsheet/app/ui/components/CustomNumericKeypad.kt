package com.quickcalcsheet.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.KeyboardReturn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickcalcsheet.app.ui.theme.EmeraldAccent
import com.quickcalcsheet.app.ui.theme.KeypadBg
import com.quickcalcsheet.app.ui.theme.SlateSurface
import com.quickcalcsheet.app.ui.theme.SlateTextPrimary

/**
 * Custom on-screen numeric keypad anchored to the bottom of the screen.
 * Contains digits 0-9, dot (.), comma (,), Enter, and Backspace.
 */
@Composable
fun CustomNumericKeypad(
    onDigitClick: (Char) -> Unit,
    onDecimalClick: (Char) -> Unit,
    onBackspaceClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KeypadBg)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: 7, 8, 9, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DigitKey(label = "7", onClick = { onDigitClick('7') })
            DigitKey(label = "8", onClick = { onDigitClick('8') })
            DigitKey(label = "9", onClick = { onDigitClick('9') })
            ActionKey(
                backgroundColor = Color(0xFFCBD5E1),
                contentColor = SlateTextPrimary,
                onClick = onBackspaceClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Backspace",
                    tint = SlateTextPrimary
                )
            }
        }

        // Row 2: 4, 5, 6, Dot (.)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DigitKey(label = "4", onClick = { onDigitClick('4') })
            DigitKey(label = "5", onClick = { onDigitClick('5') })
            DigitKey(label = "6", onClick = { onDigitClick('6') })
            DigitKey(
                label = ".",
                backgroundColor = Color(0xFFF1F5F9),
                onClick = { onDecimalClick('.') }
            )
        }

        // Row 3: 1, 2, 3, Comma (,)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DigitKey(label = "1", onClick = { onDigitClick('1') })
            DigitKey(label = "2", onClick = { onDigitClick('2') })
            DigitKey(label = "3", onClick = { onDigitClick('3') })
            DigitKey(
                label = ",",
                backgroundColor = Color(0xFFF1F5F9),
                onClick = { onDecimalClick(',') }
            )
        }

        // Row 4: 0 (wide), Enter (wide)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DigitKey(
                label = "0",
                weight = 2f,
                onClick = { onDigitClick('0') }
            )
            ActionKey(
                weight = 2f,
                backgroundColor = EmeraldAccent,
                contentColor = Color.White,
                onClick = onEnterClick
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardReturn,
                        contentDescription = "Enter",
                        tint = Color.White
                    )
                    Text(
                        text = "Enter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.DigitKey(
    label: String,
    weight: Float = 1f,
    backgroundColor: Color = SlateSurface,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .weight(weight)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary
        )
    }
}

@Composable
private fun RowScope.ActionKey(
    weight: Float = 1f,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .weight(weight)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
