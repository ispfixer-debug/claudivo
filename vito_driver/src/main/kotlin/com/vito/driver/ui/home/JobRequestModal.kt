// JobRequestModal - Undismissable job request modal
// Per RULE #8 - dismissOnBackPress=false, dismissOnClickOutside=false
package com.vito.driver.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vito.driver.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.VitoButtonStyle

@Composable
fun JobRequestModal(
    rideId: String,
    pickupAddress: String,
    destAddress: String,
    fare: String,
    distance: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { /* DO NOTHING - undismissable */ },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitoSpacing.screenHorizontal)
                .padding(bottom = VitoSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.new_job_request),
                style = VitoTheme.typography.headlineSmall,
            )

            Spacer(modifier = Modifier.height(VitoSpacing.lg))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VitoTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(stringResource(R.string.fare), style = VitoTheme.typography.titleMedium)
                        Text(fare)
                    }
                    Spacer(modifier = Modifier.height(VitoSpacing.xs))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(stringResource(R.string.distance), style = VitoTheme.typography.titleMedium)
                        Text(distance)
                    }
                }
            }

            Spacer(modifier = Modifier.height(VitoSpacing.lg))

            Text(pickupAddress, style = VitoTheme.typography.bodyMedium)
            Text("→", color = VitoTheme.colorScheme.primary)
            Text(destAddress, style = VitoTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(VitoSpacing.xl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md),
            ) {
                VitoButton(
                    text = stringResource(R.string.decline),
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    style = VitoButtonStyle.Ghost,
                )
                VitoButton(
                    text = stringResource(R.string.accept),
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    size = VitoButtonSize.Large,
                )
            }
        }
    }
}

