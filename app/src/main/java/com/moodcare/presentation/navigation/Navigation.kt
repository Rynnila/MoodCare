package com.moodcare.presentation.navigation

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodcare.presentation.screens.login_screen.LoginScreen
import com.moodcare.presentation.screens.main_screen.MainScreen
import com.moodcare.presentation.screens.welcome_screen.WelcomeScreen

@RequiresApi(35)
@Composable
fun Navigation() {
    val navHostController = rememberNavController()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = navHostController,
            startDestination = "WelcomeScreen"
        ){
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
}