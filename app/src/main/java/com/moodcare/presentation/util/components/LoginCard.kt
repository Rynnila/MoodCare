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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.moodcare.presentation.screens.login_screen.LoginEvent
import com.moodcare.presentation.screens.login_screen.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginCard(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit){
        viewModel.uiState.collectLatest {  currentSate ->
            if(currentSate.isSuccess){
                Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                navController.navigate("MainScreen")
            }
            if(currentSate.errorMessage != null){
                Toast.makeText(context, currentSate.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
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
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleChoiceSegmentedButton(
                        selectedIndex = state.selectedIndex,
                        options = listOf("Entrar", "Registrar"),
                        onOptionSelected = {
                            viewModel.onEvent(LoginEvent.OnSeletedIndexChanged(it))
                        }
                    )
                }

                if (state.selectedIndex == 1) {
                    InputOutlinedTextField(
                        value = state.name,
                        onTextChange = {
                            viewModel.onEvent(LoginEvent.OnNameChanged(it))
                        },
                        label = "Nome completo",
                    )
                }

                InputOutlinedTextField(
                    label = "Email",
                    value = state.email,
                    onTextChange = {
                        viewModel.onEvent(LoginEvent.OnEmailChanged(it))
                    }
                )

                InputOutlinedTextField(
                    label = "Senha",
                    value = state.password,
                    onTextChange = {
                        viewModel.onEvent(LoginEvent.OnPasswordChanged(it))
                    }
                )

                if (state.selectedIndex == 1) {
                    InputOutlinedTextField(
                        value = state.confirmPassword,
                        onTextChange = {
                            viewModel.onEvent(LoginEvent.OnConfirmPasswordChanged(it))
                        },
                        label = "Confirmar Senha"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    MyButtom(
                        text = if (state.selectedIndex == 0) "Entrar" else "Registrar",
                        onClick = {
                            viewModel.onEvent(LoginEvent.OnSubmit)
                        },
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
    LoginCard(navController = navController, viewModel = LoginViewModel())
}