package com.skymilk.chatapp.store.presentation.screen.auth.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType,
    value: String,
    onValueChange: (String) -> Unit
) {
    val visualTransformation =
        if (keyboardType == KeyboardType.Password) PasswordVisualTransformation()
        else VisualTransformation.None

    OutlinedTextField(
        modifier = modifier,
        singleLine = true,
        value = value,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "trailing icon"
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium
    )
}