package com.vito.driver.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoSpacing
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.VitoCard

/**
 * Driver onboarding - collects vehicle information.
 * Per PLAN.md §11.2
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var carMake by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var carColor by remember { mutableStateOf("") }
    var carPlate by remember { mutableStateOf("") }
    var carYear by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VitoColors.backgroundPrimary)
            .padding(VitoSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(VitoSpacing.md)
    ) {
        item {
            Spacer(modifier = Modifier.height(VitoSpacing.xl))
            Text(
                text = "Set up your vehicle",
                style = MaterialTheme.typography.headlineLarge,
                color = VitoColors.contentPrimary
            )
            Text(
                text = "Enter your car details to start driving",
                style = MaterialTheme.typography.bodyMedium,
                color = VitoColors.contentSecondary
            )
        }

        item {
            OutlinedTextField(
                value = carMake,
                onValueChange = { carMake = it },
                label = { Text("Car make") },
                placeholder = { Text("e.g. Toyota") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitoColors.primaryAccent,
                    unfocusedBorderColor = VitoColors.borderStrong
                )
            )
        }

        item {
            OutlinedTextField(
                value = carModel,
                onValueChange = { carModel = it },
                label = { Text("Car model") },
                placeholder = { Text("e.g. Camry") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitoColors.primaryAccent,
                    unfocusedBorderColor = VitoColors.borderStrong
                )
            )
        }

        item {
            OutlinedTextField(
                value = carColor,
                onValueChange = { carColor = it },
                label = { Text("Color") },
                placeholder = { Text("e.g. Silver") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitoColors.primaryAccent,
                    unfocusedBorderColor = VitoColors.borderStrong
                )
            )
        }

        item {
            OutlinedTextField(
                value = carPlate,
                onValueChange = { carPlate = it },
                label = { Text("License plate") },
                placeholder = { Text("e.g. ABC 1234") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitoColors.primaryAccent,
                    unfocusedBorderColor = VitoColors.borderStrong
                )
            )
        }

        item {
            OutlinedTextField(
                value = carYear,
                onValueChange = { carYear = it },
                label = { Text("Year") },
                placeholder = { Text("e.g. 2020") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VitoColors.primaryAccent,
                    unfocusedBorderColor = VitoColors.borderStrong
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(VitoSpacing.lg))
            
            VitoButton(
                text = if (isLoading) "Setting up..." else "Continue",
                onClick = { onComplete() },
                loading = isLoading,
                modifier = Modifier.fillMaxWidth(),
                size = VitoButtonSize.Large
            )
        }

        item {
            Spacer(modifier = Modifier.height(VitoSpacing.xxl))
        }
    }
}