package com.healthapp.emotional.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.healthapp.emotional.data.models.UserPreferences

@ProvidedTypeConverter
class UserPreferencesConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromString(value: String): UserPreferences {
        return gson.fromJson(value, UserPreferences::class.java)
    }
    
    @TypeConverter
    fun fromUserPreferences(preferences: UserPreferences): String {
        return gson.toJson(preferences)
    }
} 