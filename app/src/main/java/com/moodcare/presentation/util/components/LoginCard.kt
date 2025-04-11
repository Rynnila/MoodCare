package com.moodcare.presentation.util.components

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginCard(navController: NavController) {
    var nome by remember {
        mutableStateOf("")
    }
    var Email by remember {
        mutableStateOf("")
    }
    var Password by remember {
        mutableStateOf("")
    }
    var CofirmedPassword by remember {
        mutableStateOf("")
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    val options = listOf("Login", "Registrar")
    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimaryContainer),
        content = {
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleChoiceSegmentedButton(
                        selectedIndex = selectedIndex,
                        options = options,
                        onOptionSelected = {
                            selectedIndex = it
                            if(selectedIndex == 1){
                                expanded = true
                            }else{
                                expanded = false
                            }
                        }
                    )
                }
                if(expanded){
                    OutlinedTextField(
                        value = nome,
                        onValueChange = {
                            nome = it
                        },
                        label = {
                            Text(
                                text = "Nome completo"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
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
                if(expanded){
                    OutlinedTextField(
                        value = CofirmedPassword,
                        onValueChange = {
                            CofirmedPassword = it
                        },
                        label = {
                            Text(
                                text = "Confirmar Senha"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MyButtom(
                        text = "Login",
                        modifier = Modifier,
                        onClick = {
                            val auth = FirebaseAuth.getInstance()
                            auth.signInWithEmailAndPassword(Email, Password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate("MainScreen")
                                    } else {
                                        // Aqui você pode usar um estado pra exibir a mensagem de erro
                                        // Por exemplo:
                                        val errorMessage = task.exception?.message ?: "Falha no login"
                                        println("Erro ao fazer login: $errorMessage") // ou use outro estado para exibir
                                    }
                                }



//                            if(Email == "admin" && Password == "admin") {
//                                navController.navigate("MainScreen")
//                            }
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