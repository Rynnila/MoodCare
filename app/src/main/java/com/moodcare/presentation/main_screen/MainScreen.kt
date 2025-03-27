package com.moodcare.presentation.home_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.presentation.util.components.BottomBar
import com.moodcare.presentation.util.components.MainScreenPager
import com.moodcare.presentation.util.components.NavItem
import com.moodcare.presentation.util.components.TopBar

@Composable
fun MainScreen(){
    val items = listOf(
        NavItem.Home,
        NavItem.Explore,
        NavItem.Calendar,
        NavItem.Chat
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                title = "MoodCare"
            )
        },
        bottomBar = {
            BottomBar(items)
        }

    ){contentPadding ->
        MainScreenPager(contentPadding, items)
    }
}

@Composable
@Preview
fun MainScreenPreview(){
    MainScreen()
}