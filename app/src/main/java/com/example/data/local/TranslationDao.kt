package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
  @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
  fun getAllHistory(): Flow<List<TranslationEntity>>

  @Query("SELECT * FROM translation_history WHERE isStarred = 1 ORDER BY timestamp DESC")
  fun getStarredTranslations(): Flow<List<TranslationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: TranslationEntity): Long

  @Update
  suspend fun update(item: TranslationEntity)

  @Delete
  suspend fun delete(item: TranslationEntity)

  @Query("DELETE FROM translation_history")
  suspend fun clearAll()
}
