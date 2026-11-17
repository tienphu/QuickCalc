package com.quickcalcsheet.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SheetDao {
    @Query("SELECT * FROM sheets WHERE isDraft = 0 ORDER BY updatedAt DESC")
    fun observeSavedSheets(): Flow<List<SheetEntity>>

    @Query("SELECT * FROM sheets WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SheetEntity?

    @Query("SELECT * FROM sheets WHERE isDraft = 1 ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLatestDraft(): SheetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sheet: SheetEntity): Long

    @Update
    suspend fun update(sheet: SheetEntity)

    @Query("DELETE FROM sheets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
