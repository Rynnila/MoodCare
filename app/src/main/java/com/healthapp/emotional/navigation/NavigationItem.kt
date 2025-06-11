package com.healthapp.emotional.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Bottom Navigation Items
    object Home : NavigationItem(
        route = "home",
        title = "Início",
        icon = Icons.Rounded.Home
    )
    
    object Sessions : NavigationItem(
        route = "sessions",
        title = "Sessões",
        icon = Icons.Rounded.CalendarMonth
    )
    
    object Content : NavigationItem(
        route = "content",
        title = "Conteúdos",
        icon = Icons.AutoMirrored.Rounded.LibraryBooks
    )
    
    object Chat : NavigationItem(
        route = "chat",
        title = "Chat",
        icon = Icons.AutoMirrored.Rounded.Chat
    )
    
    object Explore : NavigationItem(
        route = "explore",
        title = "Explorar",
        icon = Icons.Rounded.Explore
    )

    // Drawer Menu Items
    object Profile : NavigationItem(
        route = "profile",
        title = "Minha Conta",
        icon = Icons.Rounded.Person
    )
    
    object Settings : NavigationItem(
        route = "settings",
        title = "Configurações",
        icon = Icons.Rounded.Settings
    )
    
    object Logout : NavigationItem(
        route = "logout",
        title = "Sair",
        icon = Icons.Default.ExitToApp
    )

    companion object {
        fun bottomNavigationItems() = listOf(Home, Sessions, Content, Chat, Explore)
        fun drawerNavigationItems() = listOf(Profile, Settings, Logout)
    }
} 