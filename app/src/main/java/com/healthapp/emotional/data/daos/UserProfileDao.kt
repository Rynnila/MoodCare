package com.healthapp.emotional.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthapp.emotional.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile): Long
    
    @Update
    suspend fun update(profile: UserProfile)
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfileById(userId: Long): UserProfile?
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfileFlow(userId: Long): Flow<UserProfile?>
    
    @Query("UPDATE user_profiles SET darkMode = :darkMode WHERE userId = :userId")
    suspend fun updateDarkModePreference(userId: Long, darkMode: Boolean)
    
    @Query("UPDATE user_profiles SET notificationsEnabled = :enabled WHERE userId = :userId")
    suspend fun updateNotificationPreference(userId: Long, enabled: Boolean)
    
    @Query("UPDATE user_profiles SET reminderTime = :time WHERE userId = :userId")
    suspend fun updateReminderTime(userId: Long, time: String)
} 