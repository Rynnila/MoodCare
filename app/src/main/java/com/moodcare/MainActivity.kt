package com.moodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodcare.presentation.screens.main_screen.MainScreen
import com.moodcare.presentation.screens.welcome_screen.WelcomeScreen
import com.moodcare.presentation.theme.MoodCareTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MoodCareTheme(theme = "light") {
                Navigation()
            }
        }
    }
}

@RequiresApi(35)
@Composable
fun Navigation(){
    val NavController = rememberNavController()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NavHost(
            navController = NavController,
            startDestination = "WelcomeScreen"
        ){
            composable("WelcomeScreen"){
                WelcomeScreen(navController = NavController)
            }
            composable("MainScreen"){
                MainScreen()
            }
        }
    }
}