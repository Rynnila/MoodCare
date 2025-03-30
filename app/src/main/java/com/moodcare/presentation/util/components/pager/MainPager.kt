package com.moodcare.presentation.util.components.pager

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moodcare.R
import com.moodcare.presentation.screens.main_screen.calendar_page.CalendarScreen
import com.moodcare.presentation.screens.main_screen.chat_page.ChatScreen
import com.moodcare.presentation.screens.main_screen.explore_page.ExploreScreen
import com.moodcare.presentation.screens.main_screen.home_page.HomeScreen

sealed class NavItem(
    val label: String,
    @DrawableRes
    val icon: Pair<Int, Int>
){
    data object Home: NavItem(
        label = "Home",
        icon = Pair(R.drawable.ic_home, R.drawable.ic_home_fill)
    )
    data object Explore: NavItem(
        label = "Explore",
        icon = Pair(R.drawable.ic_compass, R.drawable.ic_compass_fill)
    )
    data object Calendar: NavItem(
        label = "Calendar",
        icon = Pair(R.drawable.ic_calendar, R.drawable.ic_calendar_fill)
    )
    data object Chat: NavItem(
        label = "Chat",
        icon = Pair(R.drawable.ic_chat_bubble, R.drawable.ic_chat_bubble_fill)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenPager(
    contentPadding: PaddingValues,
    items: List<NavItem>,
    pagerState: PagerState){
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