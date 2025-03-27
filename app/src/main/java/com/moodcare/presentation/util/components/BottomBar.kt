package com.moodcare.presentation.util.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodcare.ui.theme.primary


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomBar(items: List<NavItem>, onItemSelected: (NavItem) -> Unit) {
    BottomAppBar(
        modifier = Modifier.height(60.dp),
        containerColor = primary()
    ) {
        items.forEach { navItem ->
            NavigationBarItem(
                modifier = Modifier
                    .size(20.dp),
                selected = false,
                onClick = {
                    onItemSelected(navItem)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = navItem.icon),
                        contentDescription = navItem.label
                    )
                },
                label = {
                    Text(navItem.label)
                }
            )
        }
    }
}
