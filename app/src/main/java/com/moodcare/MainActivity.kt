package com.moodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.presentation.screens.main_screen.MainScreen
import com.moodcare.presentation.screens.welcome_screen.WelcomeScreen
import com.moodcare.presentation.theme.MoodCareTheme

@OptIn(ExperimentalFoundationApi::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MoodCareTheme(theme = "light") {
                WelcomeScreen()
                }

        }

    }

}


@RequiresApi(35)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoodCareTheme(theme = "light") {
        MainScreen()
    }
}