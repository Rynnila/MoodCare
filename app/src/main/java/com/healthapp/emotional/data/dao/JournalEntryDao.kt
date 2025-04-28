package com.healthapp.emotional.data.dao

import androidx.room.*
import com.healthapp.emotional.data.models.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getJournalEntryById(id: String): JournalEntry?

    @Query("SELECT * FROM journal_entries WHERE moodEntryId = :moodEntryId")
    suspend fun getJournalEntriesForMood(moodEntryId: String): List<JournalEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(journalEntry: JournalEntry)

    @Update
    suspend fun updateJournalEntry(journalEntry: JournalEntry)

    @Delete
    suspend fun deleteJournalEntry(journalEntry: JournalEntry)
} 