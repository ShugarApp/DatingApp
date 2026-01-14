package com.dating.home.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    userId: String,
    imageUrl: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // In a real app, we would fetch user details by userId here.
    // For now, hardcoded dummy data for demo.
    val userName = "María García"
    val userAge = 25
    val userBio = "¡Hermoso día para salir a caminar! 🌞\nMe gusta el café, la naturaleza y viajar."

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AppCenterTopBar(
                title = "",
                onBack = onBack,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "$userName, $userAge",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
                Text(
                    text = userBio,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
            }

            // Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Pass",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.Green,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
