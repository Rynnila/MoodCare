package com.moodcare.presentation.util.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moodcare.R
import com.moodcare.ui.theme.onPrimary

@Composable
fun MenuIcon(){
    Icon(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .clickable { }
            .padding(5.dp),
        painter = painterResource
            (
            id = R.drawable.ic_menu
        ),
        contentDescription = "menu",
        tint = onPrimary()
    )
}