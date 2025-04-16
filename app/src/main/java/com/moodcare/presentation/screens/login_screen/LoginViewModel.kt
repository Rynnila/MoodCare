package com.moodcare.presentation.screens.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEvent(event: LoginEvent){
        when(event){
            is LoginEvent.OnNameChanged -> {
                _uiState.value = _uiState.value.copy(name = event.name)
            }
            is LoginEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email)
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password)
            }
            is LoginEvent.OnConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPassword = event.confirmPassword)
            }
            is LoginEvent.OnSeletedIndexChanged -> {
                _uiState.value = _uiState.value.copy(selectedIndex = event.Index)
            }
            is LoginEvent.OnSubmit -> {
                if(_uiState.value.selectedIndex == 0){
                    login()
                }else{
                    register()
                }
            }
        }
    }
    private fun login(){
        val state = _uiState.value

        if(state.email.isBlank() || state.password.isBlank()){
            _uiState.value = state.copy(errorMessage = "Email e senha não podem estar vazios.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            auth.signInWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        _uiState.value = state.copy(isSuccess = true)
                    }else{
                        val errorMessage = task.exception?.message ?: "Erro desconhecido"
                        _uiState.value = state.copy(
                            errorMessage = errorMessage,
                            isLoading = false
                        )
                    }
                }
        }
    }
    private fun register(){
        val state = _uiState.value

        if(state.name.isBlank() || state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Todos os campos devem ser preenchidos.")
            return
        }
        if(state.password != state.confirmPassword){
            _uiState.value = state.copy(errorMessage = "As senhas devem ser iguais.")
            return
        }
        if(state.password.length < 6){
            _uiState.value = state.copy(errorMessage = "A senha deve ter no mínimo 6 caracteres.")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            auth.createUserWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        _uiState.value = state.copy(isSuccess = true, isLoading = false)
                    }else{
                        val errorMessage = task.exception?.message ?: "Erro desconhecido"
                        _uiState.value = state.copy(
                            errorMessage = errorMessage,
                            isLoading = false
                        )

                    }
                }
        }
    }
}
