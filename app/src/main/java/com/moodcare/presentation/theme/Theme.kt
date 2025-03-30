package com.moodcare.presentation.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val lightTheme = lightColorScheme(
    primary = LIGHT_PURPLE,
    onPrimary = BLACK,
    primaryContainer = PURPLE,
    onPrimaryContainer = WHITE,
    background = LIGHT_GRAY,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color.Red,
    onError = WHITE
)

private val classicTheme = lightColorScheme(
    primary = LIGHT_PURPLE,
    onPrimary = BLACK,
    secondary = LIGHT_YELLOW,
)

@Composable
fun MoodCareTheme(
    theme: String,
    content: @Composable () -> Unit
){
    val colorScheme: ColorScheme = when(theme){
        "light" -> lightTheme
        "classic" -> classicTheme
        else -> lightTheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoodCareTypography,
        content = content
    )

    val window = (LocalContext.current as? Activity)?.window
    window?.statusBarColor = colorScheme.primaryContainer.toArgb()

}