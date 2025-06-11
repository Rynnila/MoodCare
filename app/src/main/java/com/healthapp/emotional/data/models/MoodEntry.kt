package com.healthapp.emotional.data.models

import androidx.room.*
import com.healthapp.emotional.data.converters.Converters

@Entity(
    tableName = "mood_entries",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId")
    ]
)
@TypeConverters(Converters::class)
data class MoodEntry(
    @PrimaryKey
    val id: String,
    val userId: String,
    val timestamp: Long,
    val emoji: String,
    val label: String,
    val intensity: Int,
    val note: String = ""
) 