package com.linkitsoft.notyet.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromCategory(category: DocumentCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): DocumentCategory = runCatching {
        DocumentCategory.valueOf(value)
    }.getOrDefault(DocumentCategory.ID_CARD)

    @TypeConverter
    fun fromReminderDays(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toReminderDays(value: String): List<Int> {
        if (value.isBlank()) return listOf(90, 30, 7, 1)
        return value.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
