package com.moodcare.presentation.util.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moodcare.R
import com.moodcare.presentation.calendar_screen.CalendarScreen
import com.moodcare.presentation.chat_screen.ChatScreen
import com.moodcare.presentation.explore_screen.ExploreScreen
import com.moodcare.presentation.home_screen.HomeScreen

sealed class NavItem(
    val label: String,
    @DrawableRes
    val icon: Int
){
    data object Home: NavItem(
        label = "Home",
        icon = R.drawable.ic_home
    )
    data object Explore: NavItem(
        label = "Explore",
        icon = R.drawable.ic_compass
    )
    data object Calendar: NavItem(
        label = "Calendar",
        icon = R.drawable.ic_calendar
    )
    data object Chat: NavItem(
        label = "Chat",
        icon = R.drawable.ic_chat_bubble
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenPager(contentPadding: PaddingValues, items: List<NavItem>, pagerState: PagerState){
    HorizontalPager(
        pagerState,
        Modifier
            .padding(contentPadding)
    ) { page ->
        val item = items[page]
        when (item) {
            NavItem.Home -> HomeScreen()
            NavItem.Explore -> ExploreScreen()
            NavItem.Calendar -> CalendarScreen()
            NavItem.Chat -> ChatScreen()
        }
    }
}