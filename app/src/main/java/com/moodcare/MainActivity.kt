package com.moodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.moodcare.presentation.navigation.Navigation
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

