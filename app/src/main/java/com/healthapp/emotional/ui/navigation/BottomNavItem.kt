package com.healthapp.emotional.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Sessions : BottomNavItem("sessions", "Sessões", Icons.Default.CalendarMonth)
    object Mood : BottomNavItem("mood", "Humor", Icons.Default.Mood)
    object Chat : BottomNavItem("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Explore : BottomNavItem("explore", "Explorar", Icons.Default.Explore)

    companion object {
        fun fromRoute(route: String): BottomNavItem {
            return when (route) {
                "home" -> Home
                "sessions" -> Sessions
                "mood" -> Mood
                "chat" -> Chat
                "explore" -> Explore
                else -> Home
            }
        }
    }
} 