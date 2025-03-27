package com.moodcare.presentation.util.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodcare.ui.theme.primary


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomBar(items: List<NavItem>, pagerState: PagerState, selectedItem: NavItem) {
    BottomAppBar(
        modifier = Modifier.height(50.dp),
        containerColor = primary()
    ) {
        items.forEach { NavItem ->
            NavigationBarItem(
                selected = NavItem == items[pagerState.currentPage],
                onClick = {

                },
                icon = {
                    Icon(
                        painter = painterResource(id = NavItem.icon),
                        contentDescription = NavItem.label
                    )
                }
            )
        }
    }
}
