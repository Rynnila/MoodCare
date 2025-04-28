package com.healthapp.emotional.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime

@ProvidedTypeConverter
class AvailabilityConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromString(value: String): Map<LocalDate, List<LocalTime>> {
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        val stringMap: Map<String, List<String>> = gson.fromJson(value, mapType)
        return stringMap.mapKeys { LocalDate.parse(it.key) }
                      .mapValues { entry -> entry.value.map { LocalTime.parse(it) } }
    }
    
    @TypeConverter
    fun fromMap(map: Map<LocalDate, List<LocalTime>>): String {
        val stringMap = map.mapKeys { it.key.toString() }
                          .mapValues { entry -> entry.value.map { it.toString() } }
        return gson.toJson(stringMap)
    }
} 