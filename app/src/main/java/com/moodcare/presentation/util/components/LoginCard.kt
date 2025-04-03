package com.moodcare.presentation.util.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginCard(navController: NavController) {
    var Email by remember {
        mutableStateOf("")
    }
    var Password by remember {
        mutableStateOf("")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimaryContainer),
        content = {
            Column(
                modifier = Modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Email"
                        )
                    },
                    value = Email,
                    onValueChange = {
                        Email = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Senha"
                        )
                    },
                    value = Password,
                    onValueChange = {
                        Password = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MyButtom(
                        text = "Login",
                        modifier = Modifier,
                        onClick = {
                            if(Email == "admin" && Password == "admin") {
                                navController.navigate("MainScreen")
                            }
                        }
                    )
                }
            }

        }
    )
}


@Preview
@Composable
private fun LoginCardPreview() {
    val navController = rememberNavController()
    LoginCard(navController = navController)
}