package com.moodcare.presentation.screens.start_screen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class StartViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    val isUserAuthenticated: Boolean
    get() = auth.currentUser != null
}