package com.moodcare.presentation.navigation

import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodcare.presentation.screens.login_screen.LoginScreen
import com.moodcare.presentation.screens.main_screen.MainScreen
import com.moodcare.presentation.screens.start_screen.StartScreen
import com.moodcare.presentation.screens.start_screen.StartViewModel
import com.moodcare.presentation.screens.welcome_screen.WelcomeScreen

@RequiresApi(35)
@Composable
fun Navigation() {
    val navHostController = rememberNavController()

    NavHost(
        navController = navHostController,
        startDestination = "StartScreen"
    ){
        composable("StartScreen"){
            StartScreen(navController = navHostController, viewModel = StartViewModel())
        }
        composable("LoginScreen"){
            LoginScreen(navController = navHostController)
        }
        composable("WelcomeScreen"){
            WelcomeScreen(navController = navHostController)
        }
        composable("MainScreen"){
            MainScreen()
        }
    }
}