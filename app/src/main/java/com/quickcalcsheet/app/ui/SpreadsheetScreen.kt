package com.quickcalcsheet.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickcalcsheet.app.ui.components.CustomNumericKeypad
import com.quickcalcsheet.app.ui.components.HeaderBar
import com.quickcalcsheet.app.ui.components.PwaGuideDialog
import com.quickcalcsheet.app.ui.components.SaveSheetDialog
import com.quickcalcsheet.app.ui.components.SavedSheetsDialog
import com.quickcalcsheet.app.ui.components.SpreadsheetGrid
import com.quickcalcsheet.app.ui.components.SummaryRow
import com.quickcalcsheet.app.ui.theme.SlateCanvas

@Composable
fun SpreadsheetScreen(viewModel: SpreadsheetViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toastMessage) {
        val message = state.toastMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearToast()
        }
    }

    Scaffold(
        containerColor = SlateCanvas,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Top Header
            HeaderBar(
                sheetTitle = state.sheetTitle,
                isDraft = state.isDraft,
                onNewClick = viewModel::createNewSheet,
                onSaveClick = viewModel::openSaveDialog,
                onSavedClick = viewModel::openSavedList,
                onPwaGuideClick = viewModel::openPwaGuide
            )

            // 2. Vertically Scrollable Spreadsheet Grid
            SpreadsheetGrid(
                rows = state.rows,
                cursor = state.cursor,
                onCellClick = viewModel::selectCell,
                modifier = Modifier.weight(1f)
            )

            // 3. Pinned Summary Row directly above Keypad
            SummaryRow(
                totalCol1 = state.totalCol1,
                totalCol3 = state.totalCol3
            )

            // 4. Custom Numeric Keypad at bottom (blocks default system keyboard)
            CustomNumericKeypad(
                onDigitClick = viewModel::onKeyDigit,
                onDecimalClick = viewModel::onKeyDecimal,
                onBackspaceClick = viewModel::onKeyBackspace,
                onEnterClick = viewModel::onKeyEnter
            )
        }
    }

    if (state.isSaveDialogVisible) {
        SaveSheetDialog(
            initialTitle = state.sheetTitle,
            previewFormatter = viewModel::buildFormattedTitleWithDate,
            onDismiss = viewModel::dismissSaveDialog,
            onConfirmSave = viewModel::saveSheetWithTitle
        )
    }

    if (state.isSavedListVisible) {
        SavedSheetsDialog(
            sheets = state.savedSheets,
            onDismiss = viewModel::dismissSavedList,
            onOpenSheet = viewModel::openSavedSheet,
            onDeleteSheet = viewModel::deleteSavedSheet
        )
    }

    if (state.isPwaGuideVisible) {
        PwaGuideDialog(onDismiss = viewModel::dismissPwaGuide)
    }
}
