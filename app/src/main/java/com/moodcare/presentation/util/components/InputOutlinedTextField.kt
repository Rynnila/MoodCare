package com.moodcare.presentation.util.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputOutlinedTextField(
    value: String,
    label: String,
    onTextChange: (String) -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        textStyle = MaterialTheme.typography.bodySmall,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        value = value,
        onValueChange = {
            onTextChange(it)
        },
        label = {
            Text(
                maxLines = 1,
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}
