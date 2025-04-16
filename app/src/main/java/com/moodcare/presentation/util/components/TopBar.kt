package com.moodcare.presentation.util.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moodcare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String
){
    TopAppBar(
        modifier = Modifier
            .height(70.dp),
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { }
                        .padding(5.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    painter = painterResource
                        (
                        id = R.drawable.ic_menu
                    ),
                    contentDescription = "menu"
                )
            }
        }
    )

}

@Composable
@Preview
fun TopBarPreview(){
    TopBar(title = "Preview")
}