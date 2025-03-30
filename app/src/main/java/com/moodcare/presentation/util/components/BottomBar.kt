package com.moodcare.presentation.util.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodcare.presentation.util.components.pager.NavItem


@Composable
fun BottomBar(items: List<NavItem>, onItemSelected: (NavItem) -> Unit, selectedItem: NavItem) {
    BottomAppBar(
        modifier = Modifier
            .height(60.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        items.forEach { navItem ->
            val isSelected = navItem == selectedItem
            NavigationBarItem(
                modifier = Modifier
                    .size(20.dp),
                selected = false,
                onClick = {
                    onItemSelected(navItem)
                },
                icon = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        painter = painterResource(id = if (isSelected) navItem.icon.second else navItem.icon.first),
                        contentDescription = navItem.label
                    )
                },
                label = {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        text = navItem.label
                    )
                }
            )
        }
    }
}
