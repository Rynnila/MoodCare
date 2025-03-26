package com.moodcare.presentation.util.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moodcare.ui.theme.onPrimary
import com.moodcare.ui.theme.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String
){
    TopAppBar(
        modifier = Modifier
            .height(50.dp),
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = primary(),
            titleContentColor = onPrimary()
        ),
        title = {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier,
                    color = onPrimary(),
                    text = title
                )
            }
    },
        navigationIcon = {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ){
                MenuIcon()
            }
        }
    )

}

@Composable
@Preview
fun TopBarPreview(){
    TopBar(title = "Preview")
}