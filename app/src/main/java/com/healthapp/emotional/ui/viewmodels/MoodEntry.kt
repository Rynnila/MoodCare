package com.healthapp.emotional.ui.viewmodels

data class MoodEntry(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val emoji: String,
    val label: String,
    val intensity: Int,
    val note: String = ""
) 