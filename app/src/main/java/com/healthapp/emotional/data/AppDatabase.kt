package com.healthapp.emotional.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.healthapp.emotional.data.dao.MoodEntryDao
import com.healthapp.emotional.data.dao.JournalEntryDao
import com.healthapp.emotional.data.dao.UserProfileDao
import com.healthapp.emotional.data.dao.TherapistDao
import com.healthapp.emotional.data.dao.TherapySessionDao
import com.healthapp.emotional.data.models.*
import com.healthapp.emotional.data.converters.Converters
import com.healthapp.emotional.data.converters.DateTimeConverters
import com.healthapp.emotional.data.converters.UserPreferencesConverter

@Database(
    entities = [
        User::class,
        UserProfile::class,
        MoodEntry::class,
        JournalEntry::class,
        Therapist::class,
        TherapySession::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(value = [Converters::class, DateTimeConverters::class, UserPreferencesConverter::class])
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun moodEntryDao(): MoodEntryDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun therapistDao(): TherapistDao
    abstract fun therapySessionDao(): TherapySessionDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emotional_health_db"
                )
                .fallbackToDestructiveMigration()
                .addTypeConverter(UserPreferencesConverter())
                .addTypeConverter(DateTimeConverters())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 