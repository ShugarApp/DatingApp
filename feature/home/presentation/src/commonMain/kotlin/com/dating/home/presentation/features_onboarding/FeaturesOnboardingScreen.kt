package com.dating.home.presentation.features_onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aura.feature.home.presentation.generated.resources.Res
import aura.feature.home.presentation.generated.resources.features_onboarding_dates_badge_1
import aura.feature.home.presentation.generated.resources.features_onboarding_dates_badge_2
import aura.feature.home.presentation.generated.resources.features_onboarding_dates_badge_3
import aura.feature.home.presentation.generated.resources.features_onboarding_dates_subtitle
import aura.feature.home.presentation.generated.resources.features_onboarding_dates_title
import aura.feature.home.presentation.generated.resources.features_onboarding_get_started
import aura.feature.home.presentation.generated.resources.features_onboarding_next
import aura.feature.home.presentation.generated.resources.features_onboarding_safety_badge_1
import aura.feature.home.presentation.generated.resources.features_onboarding_safety_badge_2
import aura.feature.home.presentation.generated.resources.features_onboarding_safety_badge_3
import aura.feature.home.presentation.generated.resources.features_onboarding_safety_subtitle
import aura.feature.home.presentation.generated.resources.features_onboarding_safety_title
import aura.feature.home.presentation.generated.resources.features_onboarding_skip
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.theme.extended
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val badges: List<Pair<ImageVector, String>>,
    val gradientColors: List<Color>
)

@Composable
fun FeaturesOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    val safetyGradient = listOf(
        Color(0xFF2B0A3D),
        Color(0xFF4A1060),
        Color(0xFF0E0714)
    )
    val datesGradient = listOf(
        Color(0xFF0A1A3D),
        Color(0xFF1A3060),
        Color(0xFF0E0714)
    )

    val safetyBadge1 = stringResource(Res.string.features_onboarding_safety_badge_1)
    val safetyBadge2 = stringResource(Res.string.features_onboarding_safety_badge_2)
    val safetyBadge3 = stringResource(Res.string.features_onboarding_safety_badge_3)
    val datesBadge1 = stringResource(Res.string.features_onboarding_dates_badge_1)
    val datesBadge2 = stringResource(Res.string.features_onboarding_dates_badge_2)
    val datesBadge3 = stringResource(Res.string.features_onboarding_dates_badge_3)

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Shield,
            title = stringResource(Res.string.features_onboarding_safety_title),
            subtitle = stringResource(Res.string.features_onboarding_safety_subtitle),
            badges = listOf(
                Icons.Default.PersonAdd to safetyBadge1,
                Icons.Default.LocationOn to safetyBadge2,
                Icons.Default.Security to safetyBadge3
            ),
            gradientColors = safetyGradient
        ),
        OnboardingPage(
            icon = Icons.Default.CalendarMonth,
            title = stringResource(Res.string.features_onboarding_dates_title),
            subtitle = stringResource(Res.string.features_onboarding_dates_subtitle),
            badges = listOf(
                Icons.Default.CalendarMonth to datesBadge1,
                Icons.Default.LocationOn to datesBadge2,
                Icons.Default.Notifications to datesBadge3
            ),
            gradientColors = datesGradient
        )
    )

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Scaffold(modifier = modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Top skip button
            if (!isLastPage) {
                ChirpButton(
                    text = stringResource(Res.string.features_onboarding_skip),
                    onClick = onComplete,
                    style = AppButtonStyle.TEXT,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                )
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                PageIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage
                )

                ChirpButton(
                    text = if (isLastPage) {
                        stringResource(Res.string.features_onboarding_get_started)
                    } else {
                        stringResource(Res.string.features_onboarding_next)
                    },
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    style = AppButtonStyle.PRIMARY_PURPLE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors = page.gradientColors)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 100.dp, bottom = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.extended.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Feature badges
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                page.badges.forEach { (icon, label) ->
                    FeatureBadge(icon = icon, label = label)
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
                animationSpec = tween(durationMillis = 300),
                label = "indicator_color"
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (isSelected) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
