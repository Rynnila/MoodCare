package com.moodcare.presentation.util.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moodcare.R
import com.moodcare.ui.theme.primary
import androidx.compose.ui.res.vectorResource


@Composable
fun BottomBar() {
    data class NavItem(
        val label: String,
        val icon: ImageVector
    )

    val items = listOf(
        NavItem(
            label = "Home",
            icon = ImageVector.vectorResource(id = R.drawable.ic_home)
        ),
        NavItem(
            label = "Explore",
            icon = ImageVector.vectorResource(id = R.drawable.ic_compass)
        ),
        NavItem(
            label = "Calendar",
            icon = ImageVector.vectorResource(id = R.drawable.ic_calendar)
        ),
        NavItem(
            label = "Chat",
            icon = ImageVector.vectorResource(id = R.drawable.ic_chat_bubble)
        )
    )

    BottomAppBar(
        modifier = Modifier.height(50.dp),
        containerColor = primary()
    ) {
        items.forEach { navItem ->
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label
                    )
                }
            )
        }
    }
}
