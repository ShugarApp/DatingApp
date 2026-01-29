package com.dating.core.designsystem.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import shugar.core.designsystem.generated.resources.Res as DesignSystemRes
import shugar.core.designsystem.generated.resources.logo_chirp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    TopAppBar(
        title = {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_chirp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(128.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MainTopAppBarPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MainTopAppBar()
        }
    }
}