package com.healthapp.emotional.ui.viewmodels

data class MoodLogEntryUiState(
    val id: String = "",
    val emoji: String = "😊",
    val label: String = "Happy",
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
) 