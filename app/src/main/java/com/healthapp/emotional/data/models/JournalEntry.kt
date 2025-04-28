package com.healthapp.emotional.data.models

import androidx.room.*

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = MoodEntry::class,
            parentColumns = ["id"],
            childColumns = ["moodEntryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("moodEntryId"),
        Index("userId")
    ]
)
data class JournalEntry(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val moodEntryId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val content: String = "",
    val tags: List<String> = emptyList()
) 