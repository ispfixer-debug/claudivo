// MartScreen - Order groceries
package com.vito.client.ui.mart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize

@Composable
fun MartScreen(
    viewModel: MartViewModel = hiltViewModel(),
    onOrderCreated: (String) -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = VitoSpacing.screenHorizontal),
    ) {
        Text(stringResource(R.string.mart), style = typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        Text("Shop from local marts", style = typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        VitoButton(
            text = stringResource(R.string.continue_btn),
            onClick = { /* TODO: open mart */ },
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large,
        )
    }
}

