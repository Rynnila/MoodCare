package com.moodcare.ui.theme

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WHITE = Color(255, 255, 255)
val LIGHT_PURPLE = Color(220, 170, 255)
val LIGHT_YELLOW = Color(255, 233, 121)
val BLACK = Color(0, 0, 0)

@Composable
fun primary(): Color = colorScheme.primary

@Composable
fun onPrimary(): Color = colorScheme.onPrimary

@Composable
fun secondary(): Color = colorScheme.secondary