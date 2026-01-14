package com.dating.core.designsystem.components.layouts

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.components.brand.AppBrandLogo
import com.dating.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpAdaptiveResultLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        ChirpSurface(
            modifier = Modifier.padding(innerPadding),
            header = {
                Spacer(modifier = Modifier.height(32.dp))
                AppBrandLogo()
                Spacer(modifier = Modifier.height(32.dp))
            },
            content = content
        )
    }
}

@Composable
@Preview
fun ChirpAdaptiveResultLayoutPreview() {
    AppTheme {
        ChirpAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                Text(
                    text = "Registration successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}