package com.moodcare.presentation.screens.welcome_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moodcare.R
import com.moodcare.presentation.util.components.MyButtom
import com.moodcare.presentation.util.components.pager.PageIndicator
import com.moodcare.presentation.util.components.pager.WelcomePager
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(navController: NavController) {
    val items = listOf(
        Triple(
            first = "Título",
            second = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            third = R.drawable.placeholder
        ),
        Triple(
            first = "Título",
            second = "Outra descrição aqui.",
            third = R.drawable.placeholder
        ),
        Triple(
            first = "Título",
            second = "Mais um conteúdo de exemplo.",
            third = R.drawable.placeholder
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                PageIndicator(items.size, pagerState.currentPage)

                HorizontalDivider(thickness = 3.dp, color = Color.LightGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MyButtom(
                        text = "Voltar",
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    )

                    MyButtom(
                        text = "Avançar",
                        onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < items.size - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                                else{
                                    navController.navigate("MainScreen")
                                }
                            }
                        },
                        enabled = pagerState.currentPage < items.size - 1
                    )
                }
            }
        }
    ) { contentPadding ->
        WelcomePager(pagerState = pagerState, items = items, contentPadding = contentPadding)
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
}