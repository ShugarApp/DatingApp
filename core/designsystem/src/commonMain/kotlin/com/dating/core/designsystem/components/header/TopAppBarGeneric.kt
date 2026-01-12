package com.dating.core.designsystem.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dating.core.designsystem.components.brand.ChirpHorizontalDivider

@Composable
fun TopAppBarGeneric(
    modifier: Modifier = Modifier,
    divider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 80.dp)
                .padding(
                    vertical = 20.dp,
                    horizontal = 16.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        if (divider) ChirpHorizontalDivider()
    }
}
