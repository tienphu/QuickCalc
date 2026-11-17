package com.quickcalcsheet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.quickcalcsheet.app.ui.SpreadsheetScreen
import com.quickcalcsheet.app.ui.SpreadsheetViewModel
import com.quickcalcsheet.app.ui.theme.QuickCalcTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SpreadsheetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickCalcTheme {
                SpreadsheetScreen(viewModel = viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.autoSaveNow()
    }

    override fun onStop() {
        super.onStop()
        viewModel.autoSaveNow()
    }
}
