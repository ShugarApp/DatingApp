package com.dating.home.presentation.features_onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import shugar.feature.home.presentation.generated.resources.Res
import shugar.feature.home.presentation.generated.resources.features_onboarding_dates_badge_1
import shugar.feature.home.presentation.generated.resources.features_onboarding_dates_badge_2
import shugar.feature.home.presentation.generated.resources.features_onboarding_dates_badge_3
import shugar.feature.home.presentation.generated.resources.features_onboarding_dates_subtitle
import shugar.feature.home.presentation.generated.resources.features_onboarding_dates_title
import shugar.feature.home.presentation.generated.resources.features_onboarding_get_started
import shugar.feature.home.presentation.generated.resources.features_onboarding_next
import shugar.feature.home.presentation.generated.resources.features_onboarding_safety_badge_1
import shugar.feature.home.presentation.generated.resources.features_onboarding_safety_badge_2
import shugar.feature.home.presentation.generated.resources.features_onboarding_safety_badge_3
import shugar.feature.home.presentation.generated.resources.features_onboarding_safety_subtitle
import shugar.feature.home.presentation.generated.resources.features_onboarding_safety_title
import shugar.feature.home.presentation.generated.resources.features_onboarding_skip
import com.dating.core.designsystem.components.buttons.AppButtonStyle
import com.dating.core.designsystem.components.buttons.ChirpButton
import com.dating.core.designsystem.theme.extended
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val badges: List<Pair<ImageVector, String>>,
    val gradientColors: List<Color>,
    val accentColor: Color
)

@Composable
fun FeaturesOnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    val purpleAccent = Color(0xFF9B5CFF)
    val blueAccent = Color(0xFF5C9BFF)

    val safetyGradient = listOf(
        Color(0xFF1A0B2E),
        Color(0xFF2D1B4E),
        Color(0xFF1A0B2E),
        Color(0xFF0E0714)
    )
    val datesGradient = listOf(
        Color(0xFF0B1A2E),
        Color(0xFF1B2D4E),
        Color(0xFF0B1A2E),
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
            gradientColors = safetyGradient,
            accentColor = purpleAccent
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
            gradientColors = datesGradient,
            accentColor = blueAccent
        )
    )

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(
                page = pages[pageIndex],
                isActive = pagerState.currentPage == pageIndex,
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
                    .padding(top = 52.dp, end = 20.dp)
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0E0714).copy(alpha = 0.8f),
                            Color(0xFF0E0714)
                        )
                    )
                )
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 48.dp),
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
                    .height(56.dp)
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    // Animate icon scale when page becomes active
    val iconScale = remember { Animatable(0.6f) }
    var badgesVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            iconScale.snapTo(0.6f)
            badgesVisible = false
            iconScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = EaseOutBack)
            )
            badgesVisible = true
        }
    }

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors = page.gradientColors)
        )
    ) {
        // Decorative glow behind icon
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
                .size(200.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            page.accentColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 120.dp, bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Main icon with glow ring
            Box(
                modifier = Modifier
                    .scale(iconScale.value)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                page.accentColor.copy(alpha = 0.25f),
                                page.accentColor.copy(alpha = 0.10f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    page.accentColor.copy(alpha = 0.4f),
                                    page.accentColor.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Feature badges with staggered animation
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                page.badges.forEachIndexed { index, (icon, label) ->
                    AnimatedVisibility(
                        visible = badgesVisible,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400,
                                delayMillis = index * 120,
                                easing = FastOutSlowInEasing
                            )
                        ) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(
                                durationMillis = 400,
                                delayMillis = index * 120,
                                easing = FastOutSlowInEasing
                            )
                        )
                    ) {
                        FeatureBadge(
                            icon = icon,
                            label = label,
                            accentColor = page.accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp
            ),
            color = Color.White.copy(alpha = 0.9f)
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
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.3f)
                },
                animationSpec = tween(durationMillis = 300),
                label = "indicator_color"
            )
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(if (isSelected) 28.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
