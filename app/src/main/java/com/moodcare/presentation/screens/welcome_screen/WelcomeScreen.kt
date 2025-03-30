@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.moodcare.presentation.screens.welcome_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moodcare.R
import com.moodcare.presentation.util.components.pager.PageIndicator
import com.moodcare.presentation.util.components.pager.WelcomePager

@Composable
fun WelcomeScreen(){
    val items = listOf(
        Triple(
            first = "Título",
            second = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse eu neque ullamcorper, consectetur orci quis, varius lorem. Nulla ac commodo lacus. Sed non faucibus eros. Phasellus pretium arcu ac lacus scelerisque, ut congue diam posuere. Vestibulum ac sem vitae odio aliquam lacinia. Cras eget nunc vitae urna lobortis dignissim. Morbi rutrum purus non nibh vulputate, a iaculis velit convallis. Praesent id eros a sem ornare tempor.",
            third = R.drawable.placehoder
        ),
        Triple(
            first = "Título",
            second = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse eu neque ullamcorper, consectetur orci quis, varius lorem. Nulla ac commodo lacus. Sed non faucibus eros. Phasellus pretium arcu ac lacus scelerisque, ut congue diam posuere. Vestibulum ac sem vitae odio aliquam lacinia. Cras eget nunc vitae urna lobortis dignissim. Morbi rutrum purus non nibh vulputate, a iaculis velit convallis. Praesent id eros a sem ornare tempor.",
            third = R.drawable.placehoder
        ),
        Triple(
            first = "Título",
            second = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse eu neque ullamcorper, consectetur orci quis, varius lorem. Nulla ac commodo lacus. Sed non faucibus eros. Phasellus pretium arcu ac lacus scelerisque, ut congue diam posuere. Vestibulum ac sem vitae odio aliquam lacinia. Cras eget nunc vitae urna lobortis dignissim. Morbi rutrum purus non nibh vulputate, a iaculis velit convallis. Praesent id eros a sem ornare tempor.",
            third = R.drawable.placehoder
        )
    )
    val pagerState = rememberPagerState{
        items.size
    }
    Scaffold(
        bottomBar = {
            PageIndicator(items.size, pagerState.currentPage)
        }
    ) {
        contentPadding ->
            WelcomePager(pagerState, items, contentPadding)
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    WelcomeScreen()
}