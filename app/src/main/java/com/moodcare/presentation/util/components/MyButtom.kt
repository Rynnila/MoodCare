package com.moodcare.presentation.util.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MyButtom(
    modifier: Modifier = Modifier,
    text: String = "MyButton",
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: ButtonElevation? = null,
    onClick: () -> Unit = {},
    enabled: Boolean = true
){
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(color),
        onClick = onClick,
        elevation = elevation,
        enabled = enabled
    ) {
        Text(
            color = textColor,
            text = text
        )
    }
}

@Preview
@Composable
private fun MyButtomPreview() {
    MyButtom()
}