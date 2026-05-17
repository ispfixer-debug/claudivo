// WalletScreen - View balance and transactions
package com.vito.client.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTypography
import com.vito.design.VitoTheme

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    val typography = VitoTypography
    Column(modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal)) {
        Text(stringResource(R.string.wallet), style = typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(VitoSpacing.lg)) {
                Text(stringResource(R.string.balance), style = typography.bodyMedium)
                Text(s.balance, style = typography.displaySmall)
            }
        }
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        Text(stringResource(R.string.transactions), style = typography.titleMedium)
    }
}

