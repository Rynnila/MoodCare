package com.healthapp.emotional.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.healthapp.emotional.data.converters.UserPreferencesConverter

@Entity(tableName = "user_profiles")
@TypeConverters(UserPreferencesConverter::class)
data class UserProfile(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val preferences: UserPreferences = UserPreferences()
) 