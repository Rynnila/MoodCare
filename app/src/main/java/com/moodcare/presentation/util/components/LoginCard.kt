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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginCard(navController: NavController) {
    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var Email by remember { mutableStateOf("") }
    var Password by remember { mutableStateOf("") }
    var CofirmedPassword by remember { mutableStateOf("") }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Entrar", "Registrar")
    var expanded by remember { mutableStateOf(false) }

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
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleChoiceSegmentedButton(
                        selectedIndex = selectedIndex,
                        options = options,
                        onOptionSelected = {
                            selectedIndex = it
                            expanded = selectedIndex == 1
                        }
                    )
                }

                if (expanded) {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    label = { Text("Email") },
                    value = Email,
                    onValueChange = { Email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    label = { Text("Senha") },
                    value = Password,
                    onValueChange = { Password = it },
                    modifier = Modifier.fillMaxWidth()
                )

                if (expanded) {
                    OutlinedTextField(
                        value = CofirmedPassword,
                        onValueChange = { CofirmedPassword = it },
                        label = { Text("Confirmar Senha") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MyButtom(
                        text = if (selectedIndex == 0) "Entrar" else "Registrar",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val auth = FirebaseAuth.getInstance()
                            val emailRegex = Regex("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")

                            when {
                                Email.isBlank() || Password.isBlank() -> {
                                    Toast.makeText(
                                        context,
                                        "Email e senha não podem estar vazios.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                !emailRegex.matches(Email) -> {
                                    Toast.makeText(
                                        context,
                                        "Formato de email inválido.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                selectedIndex == 0 -> {
                                    // Login
                                    auth.signInWithEmailAndPassword(Email, Password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Login realizado com sucesso!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigate("MainScreen")
                                            } else {
                                                val errorCode = task.exception?.message?.lowercase() ?: ""
                                                val errorMessage = when {
                                                    "password is invalid" in errorCode -> "Senha incorreta. Tente novamente."
                                                    "no user record" in errorCode -> "E-mail não cadastrado."
                                                    "too many requests" in errorCode -> "Muitas tentativas. Tente novamente mais tarde."
                                                    else -> "Erro ao fazer login. Verifique os dados e tente novamente."
                                                }

                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }

                                selectedIndex == 1 -> {
                                    // Registrar
                                    when {
                                        Password.length < 6 -> {
                                            Toast.makeText(
                                                context,
                                                "A senha deve conter pelo menos 6 caracteres.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        Password != CofirmedPassword -> {
                                            Toast.makeText(
                                                context,
                                                "As senhas não coincidem.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        else -> {
                                            auth.createUserWithEmailAndPassword(Email, Password)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Cadastro realizado com sucesso!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate("MainScreen")
                                                    } else {
                                                        val errorCode = task.exception?.message?.lowercase() ?: ""
                                                        val errorMessage = when {
                                                            "password is invalid" in errorCode -> "Senha inválida. Tente novamente."
                                                            "email address is already in use" in errorCode -> "Este e-mail já está em uso."
                                                            else -> "Erro ao registrar. Tente novamente."
                                                        }

                                                        Toast.makeText(
                                                            context,
                                                            errorMessage,
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                                }
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