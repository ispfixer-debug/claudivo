package com.vito.design.component.layout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vito.design.VitoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitoScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = {}, colors = TopAppBarDefaults.topAppBarColors(containerColor = VitoTheme.colorScheme.surface)) },
        bottomBar = bottomBar,
        containerColor = VitoTheme.colorScheme.surface
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).systemBarsPadding().navigationBarsPadding()) {
            content()
        }
    }
}
