package com.healthapp.emotional.data.models

import java.time.LocalDate

data class MoodLogEntry(
    val id: String,
    val timestamp: Long,
    val emoji: String,
    val label: String,
    val moodLevel: Int,
    val description: String = "",
    val date: LocalDate = LocalDate.now()
) 