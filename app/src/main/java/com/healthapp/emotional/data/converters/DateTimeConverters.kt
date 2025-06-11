package com.healthapp.emotional.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@ProvidedTypeConverter
class DateTimeConverters @Inject constructor() {
    private val gson = Gson()

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.format(DateTimeFormatter.ISO_LOCAL_TIME)

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromDateTimeMap(map: Map<LocalDate, List<LocalTime>>?): String? = gson.toJson(map)

    @TypeConverter
    fun toDateTimeMap(value: String?): Map<LocalDate, List<LocalTime>>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<LocalDate, List<LocalTime>>>() {}.type
        return gson.fromJson(value, mapType)
    }
}