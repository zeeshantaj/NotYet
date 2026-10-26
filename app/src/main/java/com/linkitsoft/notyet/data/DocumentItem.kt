package com.linkitsoft.notyet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: DocumentCategory,
    val expiryEpochDay: Long,
    val reminderDays: List<Int> = listOf(90, 30, 7, 1),
    val notes: String = "",
)
