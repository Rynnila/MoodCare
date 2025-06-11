package com.healthapp.emotional.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.healthapp.emotional.data.models.SessionStatus

@ProvidedTypeConverter
object Converters {
    private val gson = Gson()

    // List Converters
    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>?): String? = gson.toJson(value)

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Enum Converters
    @TypeConverter
    @JvmStatic
    fun fromSessionStatus(value: SessionStatus?): String? = value?.name

    @TypeConverter
    @JvmStatic
    fun toSessionStatus(value: String?): SessionStatus? = value?.let { SessionStatus.valueOf(it) }
} 