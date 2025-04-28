package com.healthapp.emotional.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.healthapp.emotional.data.AppDatabase
import com.healthapp.emotional.data.UserDao
import com.healthapp.emotional.data.dao.MoodEntryDao
import com.healthapp.emotional.data.dao.TherapistDao
import com.healthapp.emotional.data.dao.UserProfileDao
import com.healthapp.emotional.data.dao.TherapySessionDao
import com.healthapp.emotional.data.converters.DateTimeConverters
import com.healthapp.emotional.data.converters.UserPreferencesConverter
import com.healthapp.emotional.data.repositories.MoodRepository
import com.healthapp.emotional.data.repositories.TherapistRepository
import com.healthapp.emotional.data.repositories.TherapySessionRepository
import com.healthapp.emotional.data.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideUserPreferencesConverter(): UserPreferencesConverter {
        return UserPreferencesConverter()
    }
    
    @Provides
    @Singleton
    fun provideDateTimeConverters(): DateTimeConverters {
        return DateTimeConverters()
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        userPreferencesConverter: UserPreferencesConverter,
        dateTimeConverters: DateTimeConverters
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "emotional_health_db"
        )
        .addTypeConverter(userPreferencesConverter)
        .addTypeConverter(dateTimeConverters)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }
    
    @Provides
    @Singleton
    fun provideMoodEntryDao(database: AppDatabase): MoodEntryDao {
        return database.moodEntryDao()
    }
    
    @Provides
    @Singleton
    fun provideTherapistDao(database: AppDatabase): TherapistDao {
        return database.therapistDao()
    }
    
    @Provides
    @Singleton
    fun provideTherapySessionDao(database: AppDatabase): TherapySessionDao {
        return database.therapySessionDao()
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }
    
    @Provides
    @Singleton
    fun provideMoodRepository(moodEntryDao: MoodEntryDao): MoodRepository {
        return MoodRepository(moodEntryDao)
    }
    
    @Provides
    @Singleton
    fun provideTherapistRepository(therapistDao: TherapistDao): TherapistRepository {
        return TherapistRepository(therapistDao)
    }
    
    @Provides
    @Singleton
    fun provideTherapySessionRepository(sessionDao: TherapySessionDao): TherapySessionRepository {
        return TherapySessionRepository(sessionDao)
    }
    
    @Provides
    @Singleton
    fun provideChatRepository(@ApplicationContext context: Context): ChatRepository {
        return ChatRepository(context)
    }
} 