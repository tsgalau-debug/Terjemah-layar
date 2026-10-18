package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val originalText: String,
  val translatedText: String,
  val sourceContext: String = "Layar Terjemahan",
  val engineUsed: String = "Hybrid",
  val timestamp: Long = System.currentTimeMillis(),
  val isStarred: Boolean = false,
  val blockCount: Int = 1
)
