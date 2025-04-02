@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.moodcare.presentation.util.components.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moodcare.presentation.theme.MoodCareTypography

@Composable
fun WelcomePager(pagerState: PagerState, items: List<Triple<String, String, Int>>, contentPadding: PaddingValues){
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(contentPadding),
        pageContent = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .size(500.dp)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(15.dp)),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = items[it].third),
                        contentDescription = null)
                }
                Column(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .size(500.dp)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                    Text(
                        text = items[it].first,
                        style = MoodCareTypography.titleLarge
                    )
                    Text(
                        text = items[it].second,
                        style = MoodCareTypography.bodyLarge
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun WelcomePagerPreview() {

}