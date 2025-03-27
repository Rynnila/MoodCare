package com.moodcare.presentation.main_screen

import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.presentation.util.components.BottomBar
import com.moodcare.presentation.util.components.MainScreenPager
import com.moodcare.presentation.util.components.NavItem
import com.moodcare.presentation.util.components.TopBar

@RequiresApi(35)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(){
    val items = listOf(
        NavItem.Home,
        NavItem.Explore,
        NavItem.Calendar,
        NavItem.Chat
    )
    val pagerState = rememberPagerState{
        items.size
    }
    var selectedItem by remember {
        mutableStateOf(items.first())
    }
    LaunchedEffect(selectedItem){
        pagerState.animateScrollToPage(
            items.indexOf(selectedItem)
        )
    }
    LaunchedEffect(pagerState.targetPage) {
        selectedItem = items[pagerState.targetPage]
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopBar(
                title = items[pagerState.currentPage].label
            )
        },
        bottomBar = {
            BottomBar(items, pagerState, selectedItem)
        }

    ){
        contentPadding ->
        MainScreenPager(contentPadding, items, pagerState)
    }
}

@Composable
@Preview
fun MainScreenPreview(){
    MainScreen()
}