package com.moodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.presentation.main_screen.MainScreen
import com.moodcare.ui.theme.MoodCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            MoodCareTheme(theme = "light") {
                MainScreen()
                    }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoodCareTheme(theme = "light") {
        MainScreen()
    }
}