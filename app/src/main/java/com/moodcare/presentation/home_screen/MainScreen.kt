package com.moodcare.presentation.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.presentation.util.components.BottomBar
import com.moodcare.presentation.util.components.TopBar

@Composable
fun MainScreen(){
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                title = "MoodCare"
            )
        },
        bottomBar = {
            BottomBar()
        }

    ){contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {

        }
    }
}

@Composable
@Preview
fun MainScreenPreview(){
    MainScreen()
}