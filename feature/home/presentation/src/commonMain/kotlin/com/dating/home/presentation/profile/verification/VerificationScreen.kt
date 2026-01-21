package com.dating.home.presentation.profile.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.verification_button
import aura.feature.home.presentation.generated.resources.verification_desc
import aura.feature.home.presentation.generated.resources.verification_feature_1_desc
import aura.feature.home.presentation.generated.resources.verification_feature_1_title
import aura.feature.home.presentation.generated.resources.verification_feature_2_desc
import aura.feature.home.presentation.generated.resources.verification_feature_2_title
import aura.feature.home.presentation.generated.resources.verification_feature_3_desc
import aura.feature.home.presentation.generated.resources.verification_feature_3_title
import aura.feature.home.presentation.generated.resources.verification_footer
import aura.feature.home.presentation.generated.resources.verification_subtitle
import aura.feature.home.presentation.generated.resources.verification_title
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.components.header.AppCenterTopBar
import com.dating.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AppCenterTopBar(
                title = stringResource(Res.string.verification_title),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Hero Section (Placeholder for complex animation/graphic)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(32.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.verification_subtitle),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colorScheme.extended.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(Res.string.verification_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature List
            SecurityFeatureItem(
                icon = Icons.Default.Lock,
                title = stringResource(Res.string.verification_feature_1_title),
                description = stringResource(Res.string.verification_feature_1_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecurityFeatureItem(
                icon = Icons.Default.VerifiedUser,
                title = stringResource(Res.string.verification_feature_2_title),
                description = stringResource(Res.string.verification_feature_2_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SecurityFeatureItem(
                icon = Icons.Default.Security,
                title = stringResource(Res.string.verification_feature_3_title),
                description = stringResource(Res.string.verification_feature_3_desc)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ChirpButton(
                text = stringResource(Res.string.verification_button),
                onClick = { /* TODO: Start verification flow */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.verification_footer),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.extended.textDisabled
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SecurityFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary
            )
        }
    }
}
