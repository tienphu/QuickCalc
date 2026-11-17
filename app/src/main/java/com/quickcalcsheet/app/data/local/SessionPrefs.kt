package com.quickcalcsheet.app.data.local

import android.content.Context
import androidx.core.content.edit

class SessionPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("quickcalc_session", Context.MODE_PRIVATE)

    var activeSheetId: Long
        get() = prefs.getLong(KEY_ACTIVE_SHEET_ID, NO_SHEET_ID)
        set(value) = prefs.edit { putLong(KEY_ACTIVE_SHEET_ID, value) }

    var cursorRow: Int
        get() = prefs.getInt(KEY_CURSOR_ROW, 0)
        set(value) = prefs.edit { putInt(KEY_CURSOR_ROW, value) }

    var cursorColumn: String
        get() = prefs.getString(KEY_CURSOR_COLUMN, "COL_1") ?: "COL_1"
        set(value) = prefs.edit { putString(KEY_CURSOR_COLUMN, value) }

    fun saveCursor(row: Int, column: String) {
        prefs.edit {
            putInt(KEY_CURSOR_ROW, row)
            putString(KEY_CURSOR_COLUMN, column)
        }
    }

    companion object {
        const val NO_SHEET_ID = -1L
        private const val KEY_ACTIVE_SHEET_ID = "active_sheet_id"
        private const val KEY_CURSOR_ROW = "cursor_row"
        private const val KEY_CURSOR_COLUMN = "cursor_column"
    }
}
