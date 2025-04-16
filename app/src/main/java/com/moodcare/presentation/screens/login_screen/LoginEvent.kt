package com.moodcare.presentation.screens.login_screen

sealed  class LoginEvent {
    data class OnNameChanged(val name: String) : LoginEvent()
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : LoginEvent()
    data class OnSeletedIndexChanged(val Index: Int) : LoginEvent()
    object OnSubmit : LoginEvent()
}