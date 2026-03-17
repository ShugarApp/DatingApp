package com.dating.home.presentation.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.empty_messages
import com.dating.core.designsystem.theme.AppTheme
import com.dating.core.presentation.util.DeviceConfiguration
import com.dating.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptySection(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    illustration: DrawableResource = Res.drawable.empty_messages
) {
    val configuration = currentDeviceConfiguration()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(illustration),
            contentDescription = null,
            modifier = Modifier.size(
                if (configuration == DeviceConfiguration.MOBILE_LANDSCAPE) {
                    125.dp
                } else {
                    180.dp
                }
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        EmptySection(
            title = "Aún no tienes conversaciones",
            description = "Haz match con alguien y envíale el primer mensaje.",
        )
    }
}