package com.healthapp.emotional.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.healthapp.emotional.data.converters.DateTimeConverters
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "therapists")
@TypeConverters(DateTimeConverters::class)
data class Therapist(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val specialization: String = "",
    val bio: String = "",
    val photoUrl: String = "",
    val rating: Float = 0f,
    val isAvailable: Boolean = true,
    val availability: Map<LocalDate, List<LocalTime>> = emptyMap(),
    val description: String = "",
    val isOnline: Boolean = false,
    val price: Double = 0.0
) 