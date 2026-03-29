package com.dating.aura.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.composeapp.generated.resources.Res
import aura.composeapp.generated.resources.blocked_banned_desc
import aura.composeapp.generated.resources.blocked_banned_title
import aura.composeapp.generated.resources.blocked_contact_support
import aura.composeapp.generated.resources.blocked_deleted_desc
import aura.composeapp.generated.resources.blocked_deleted_title
import aura.composeapp.generated.resources.blocked_logout
import aura.composeapp.generated.resources.blocked_suspended_desc
import aura.composeapp.generated.resources.blocked_suspended_title
import com.dating.core.domain.auth.UserStatus
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlockedScreen(
    userStatus: UserStatus,
    onLogout: () -> Unit
) {
    val (title, description, icon) = when (userStatus) {
        UserStatus.BANNED -> Triple(
            Res.string.blocked_banned_title,
            Res.string.blocked_banned_desc,
            "\u26D4"
        )
        UserStatus.DELETED -> Triple(
            Res.string.blocked_deleted_title,
            Res.string.blocked_deleted_desc,
            "\uD83D\uDDD1\uFE0F"
        )
        else -> Triple(
            Res.string.blocked_suspended_title,
            Res.string.blocked_suspended_desc,
            "\u26A0\uFE0F"
        )
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 36.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.blocked_contact_support),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onLogout) {
                Text(
                    text = stringResource(Res.string.blocked_logout),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
