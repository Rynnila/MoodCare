package com.healthapp.emotional.data.models

data class UserPreferences(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "20:00",
    val weeklyReport: Boolean = true
) 