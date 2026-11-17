package com.quickcalcsheet.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

data class SheetRowData(
    val id: Long,
    val col1: String = "",
    val col2: String = ""
)

@Entity(tableName = "sheets")
data class SheetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val isDraft: Boolean,
    val rowsJson: String,
    val rowCount: Int,
    val totalCol1: String,
    val totalCol3: String,
    val createdAt: Long,
    val updatedAt: Long
)

object SheetRowCodec {
    fun encode(rows: List<SheetRowData>): String {
        val array = JSONArray()
        rows.forEach { row ->
            val obj = JSONObject()
            obj.put("id", row.id)
            obj.put("col1", row.col1)
            obj.put("col2", row.col2)
            array.put(obj)
        }
        return array.toString()
    }

    fun decode(json: String): List<SheetRowData> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val obj = array.getJSONObject(index)
                SheetRowData(
                    id = obj.optLong("id", (index + 1).toLong()),
                    col1 = obj.optString("col1", ""),
                    col2 = obj.optString("col2", "")
                )
            }
        }.getOrDefault(emptyList())
    }
}
