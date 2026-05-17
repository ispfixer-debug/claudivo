package com.vito.design.component.input
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme

@Composable
fun VitoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    hint: String = "",
    isError: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = VitoTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(VitoSpacing.xs))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder = { Text(hint, color = VitoTheme.colorScheme.onSurfaceVariant) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VitoTheme.colorScheme.primary,
                unfocusedBorderColor = VitoTheme.colorScheme.outline
            )
        )
    }
}
