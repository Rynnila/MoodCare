package com.healthapp.emotional.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthapp.emotional.data.models.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(moodEntry: MoodEntry): Long
    
    @Update
    suspend fun update(moodEntry: MoodEntry)
    
    @Delete
    suspend fun delete(moodEntry: MoodEntry)
    
    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodEntriesByUserId(userId: Long): Flow<List<MoodEntry>>
    
    @Query("SELECT * FROM mood_entries WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getMoodEntriesInTimeRange(userId: Long, startTime: Long, endTime: Long): Flow<List<MoodEntry>>
    
    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodEntryById(id: Long): MoodEntry?
    
    @Query("SELECT COUNT(*) FROM mood_entries WHERE userId = :userId")
    suspend fun getMoodEntryCount(userId: Long): Int
} 