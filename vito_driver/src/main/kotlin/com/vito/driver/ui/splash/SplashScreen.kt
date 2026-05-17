package com.vito.driver.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoTheme
import kotlinx.coroutines.delay

/**
 * Driver Splash screen - app identity with car icon.
 */
@Composable
fun DriverSplashScreen(
    onTimeout: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(2000)
        isVisible = false
        delay(300)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VitoColors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        VitoColors.primaryAccent,
                        androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "v",
                    style = MaterialTheme.typography.displayMedium,
                    color = VitoColors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Vito Driver",
                style = MaterialTheme.typography.titleLarge,
                color = VitoColors.contentPrimary
            )
        }
    }
}