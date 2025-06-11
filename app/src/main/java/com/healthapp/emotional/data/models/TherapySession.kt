package com.healthapp.emotional.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.healthapp.emotional.data.converters.DateConverter
import com.healthapp.emotional.data.converters.LocalTimeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

enum class SessionStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "therapy_sessions")
@TypeConverters(DateConverter::class, LocalTimeConverter::class)
data class TherapySession(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val therapistName: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val duration: Int, // in minutes
    val notes: String = "",
    val location: String = "",
    val mode: String = "", // "in-person", "online", "phone"
    val status: SessionStatus = SessionStatus.SCHEDULED,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)