package com.healthapp.emotional.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.healthapp.emotional.ui.screens.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthapp.emotional.ui.viewmodels.MainViewModel
import com.healthapp.emotional.data.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Composable
fun NavGraph(
    navController: NavHostController,
    chatRepository: ChatRepository,
    onLogout: () -> Unit
) {
    val viewModel: MainViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Sessions.route) {
            SessionsScreen()
        }
        composable(BottomNavItem.Mood.route) {
            MoodScreen()
        }
        composable(BottomNavItem.Chat.route) {
            ChatScreen(chatRepository = chatRepository)
        }
        composable(BottomNavItem.Explore.route) {
            ExploreScreen()
        }
        composable("profile") {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("settings") {
            SettingsScreen()
        }
    }
} 