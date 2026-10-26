package com.linkitsoft.notyet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY expiryEpochDay ASC")
    fun getAllDocumentsSortedByExpiry(): Flow<List<DocumentItem>>

    @Query("SELECT * FROM documents ORDER BY expiryEpochDay ASC")
    suspend fun getAllDocumentsDirect(): List<DocumentItem>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): DocumentItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentItem): Long

    @Update
    suspend fun updateDocument(document: DocumentItem): Int

    @Delete
    suspend fun deleteDocument(document: DocumentItem): Int

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long): Int
}
