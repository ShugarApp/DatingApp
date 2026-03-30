package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.attach_media
import shugar.feature.home.presentation.generated.resources.pick_from_gallery
import shugar.feature.home.presentation.generated.resources.record_audio
import org.jetbrains.compose.resources.stringResource

enum class MediaPickerOption {
    GALLERY,
    GIF,
    AUDIO
}

@Composable
fun MediaPickerContent(
    onOptionSelected: (MediaPickerOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(Res.string.attach_media),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        MediaPickerOptionItem(
            icon = Icons.Default.PhotoLibrary,
            label = stringResource(Res.string.pick_from_gallery),
            onClick = { onOptionSelected(MediaPickerOption.GALLERY) }
        )
        MediaPickerOptionItem(
            icon = Icons.Default.Gif,
            label = "GIF",
            onClick = { onOptionSelected(MediaPickerOption.GIF) }
        )
        MediaPickerOptionItem(
            icon = Icons.Default.Mic,
            label = stringResource(Res.string.record_audio),
            onClick = { onOptionSelected(MediaPickerOption.AUDIO) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MediaPickerOptionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
