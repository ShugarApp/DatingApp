package com.dating.home.presentation.emergency.onboarding

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.emergency_onboarding_done
import aura.feature.home.presentation.generated.resources.emergency_onboarding_next
import aura.feature.home.presentation.generated.resources.emergency_onboarding_skip
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step1_desc
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step1_title
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step2_desc
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step2_title
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step3_desc
import aura.feature.home.presentation.generated.resources.emergency_onboarding_step3_title
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.presentation.util.ObserveAsEvents
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmergencyOnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmergencyOnboardingViewModel = koinViewModel()
) {
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EmergencyOnboardingEvent.OnFinished -> onFinished()
        }
    }

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Security,
            iconTint = MaterialTheme.colorScheme.primary,
            title = stringResource(Res.string.emergency_onboarding_step1_title),
            description = stringResource(Res.string.emergency_onboarding_step1_desc)
        ),
        OnboardingPage(
            icon = Icons.Default.ContactPhone,
            iconTint = MaterialTheme.colorScheme.tertiary,
            title = stringResource(Res.string.emergency_onboarding_step2_title),
            description = stringResource(Res.string.emergency_onboarding_step2_desc)
        ),
        OnboardingPage(
            icon = Icons.Default.Sos,
            iconTint = Color(0xFFE53935),
            title = stringResource(Res.string.emergency_onboarding_step3_title),
            description = stringResource(Res.string.emergency_onboarding_step3_desc)
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLastPage) {
                    TextButton(onClick = { viewModel.onAction(EmergencyOnboardingAction.OnSkip) }) {
                        Text(
                            text = stringResource(Res.string.emergency_onboarding_skip),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages.indices.forEach { index ->
                    val color by animateColorAsState(
                        targetValue = if (index == pagerState.currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ChirpButton(
                text = if (isLastPage) {
                    stringResource(Res.string.emergency_onboarding_done)
                } else {
                    stringResource(Res.string.emergency_onboarding_next)
                },
                onClick = {
                    if (isLastPage) {
                        viewModel.onAction(EmergencyOnboardingAction.OnFinish)
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(page.iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = page.iconTint
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val description: String
)
