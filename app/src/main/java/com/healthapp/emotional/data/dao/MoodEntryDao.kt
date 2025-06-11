package com.healthapp.emotional.data.dao

import androidx.room.*
import com.healthapp.emotional.data.models.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodEntryById(id: String): MoodEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntry)

    @Update
    suspend fun updateMoodEntry(moodEntry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)
} 