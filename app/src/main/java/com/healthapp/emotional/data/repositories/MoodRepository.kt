package com.healthapp.emotional.data.repositories

import com.healthapp.emotional.data.dao.MoodEntryDao
import com.healthapp.emotional.data.models.MoodEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling mood entries data operations
 */
@Singleton
class MoodRepository @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) {
    /**
     * Get all mood entries for a specific user
     */
    suspend fun getMoodEntriesForUser(userId: String): List<MoodEntry> = withContext(Dispatchers.IO) {
        return@withContext moodEntryDao.getAllMoodEntries().first().filter { it.userId == userId }
    }

    /**
     * Add a new mood entry
     */
    suspend fun addMoodEntry(moodEntry: MoodEntry) = withContext(Dispatchers.IO) {
        moodEntryDao.insertMoodEntry(moodEntry)
    }

    /**
     * Update an existing mood entry
     */
    suspend fun updateMoodEntry(moodEntry: MoodEntry) = withContext(Dispatchers.IO) {
        moodEntryDao.updateMoodEntry(moodEntry)
    }

    /**
     * Delete a mood entry
     */
    suspend fun deleteMoodEntry(moodEntry: MoodEntry) = withContext(Dispatchers.IO) {
        moodEntryDao.deleteMoodEntry(moodEntry)
    }

    /**
     * Get a single mood entry by id
     */
    suspend fun getMoodEntryById(id: String): MoodEntry? = withContext(Dispatchers.IO) {
        return@withContext moodEntryDao.getMoodEntryById(id)
    }
} 