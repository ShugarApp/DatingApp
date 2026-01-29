package com.dating.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,

    // Accent colors
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,

    // Cake colors for chat bubbles
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
)

val LightExtendedColors = ExtendedColors(
    primaryHover = ChirpBrand600,
    destructiveHover = ChirpRed600,
    destructiveSecondaryOutline = ChirpRed200,
    disabledOutline = ChirpBase200,
    disabledFill = ChirpBase100,
    successOutline = ChirpBrand100,
    success = ChirpBrand500,
    onSuccess = ChirpBase0,
    secondaryFill = ChirpBase100,

    textPrimary = ChirpBase900,
    textTertiary = ChirpBrand1000,
    textSecondary = ChirpBase700,
    textPlaceholder = ChirpBase700,
    textDisabled = ChirpBase200,

    surfaceLower = ChirpBase50,
    surfaceHigher = ChirpBase0,
    surfaceOutline = ChirpBase200,
    overlay = Color(0x99000000),

    accentBlue = AccentPremium,
    accentPurple = ChirpBrand500,
    accentViolet = ChirpBrand600,
    accentPink = ChirpBrand500,
    accentOrange = Color(0xFFFFA726),
    accentYellow = AccentVip,
    accentGreen = Color(0xFF2ECC71),
    accentTeal = Color(0xFF26A69A),
    accentLightBlue = Color(0xFF4BB0FF),
    accentGrey = ChirpBase200,

    cakeViolet = ChirpCakeLightViolet,
    cakeGreen = ChirpCakeLightGreen,
    cakeBlue = ChirpCakeLightBlue,
    cakePink = ChirpCakeLightPink,
    cakeOrange = ChirpCakeLightOrange,
    cakeYellow = ChirpCakeLightYellow,
    cakeTeal = ChirpCakeLightTeal,
    cakePurple = ChirpCakeLightPurple,
    cakeRed = ChirpCakeLightRed,
    cakeMint = ChirpCakeLightMint,
)

val DarkExtendedColors = ExtendedColors(
    primaryHover = ChirpBrand600,
    destructiveHover = ChirpRed600,
    destructiveSecondaryOutline = ChirpRed600,
    disabledOutline = ChirpBase900D,
    disabledFill = ChirpBase950,
    successOutline = ChirpBrand600,
    success = ChirpBrand500,
    onSuccess = ChirpBase1000,
    secondaryFill = ChirpBase900D,

    textPrimary = ChirpBase0,
    textTertiary = ChirpBrand1000,
    textSecondary = ChirpBase400D,
    textPlaceholder = ChirpBase400D,
    textDisabled = ChirpBase900D,

    surfaceLower = ChirpBase1000,
    surfaceHigher = ChirpBase950,
    surfaceOutline = ChirpBase900D,
    overlay = Color(0xCC000000),

    accentBlue = AccentPremiumDark,
    accentPurple = ChirpBrand500,
    accentViolet = ChirpBrand600,
    accentPink = ChirpBrand500,
    accentOrange = Color(0xFFFFB74D),
    accentYellow = AccentVipDark,
    accentGreen = Color(0xFF66BB6A),
    accentTeal = Color(0xFF4DB6AC),
    accentLightBlue = Color(0xFF6BC1FF),
    accentGrey = ChirpBase900D,

    cakeViolet = ChirpCakeDarkViolet,
    cakeGreen = ChirpCakeDarkGreen,
    cakeBlue = ChirpCakeDarkBlue,
    cakePink = ChirpCakeDarkPink,
    cakeOrange = ChirpCakeDarkOrange,
    cakeYellow = ChirpCakeDarkYellow,
    cakeTeal = ChirpCakeDarkTeal,
    cakePurple = ChirpCakeDarkPurple,
    cakeRed = ChirpCakeDarkRed,
    cakeMint = ChirpCakeDarkMint,
)

val LightColorScheme = lightColorScheme(
    primary = ChirpBrand500,
    onPrimary = ChirpBase0,
    primaryContainer = ChirpBrand100,
    onPrimaryContainer = ChirpBrand900,

    secondary = ChirpBase700,
    onSecondary = ChirpBase0,
    secondaryContainer = ChirpBase100,
    onSecondaryContainer = ChirpBase900,

    tertiary = ChirpBrand600,
    onTertiary = ChirpBase0,
    tertiaryContainer = ChirpBrand100,
    onTertiaryContainer = ChirpBrand900,

    error = ChirpRed500,
    onError = ChirpBase0,
    errorContainer = ChirpRed200,
    onErrorContainer = ChirpRed600,

    background = ChirpBase50,
    onBackground = ChirpBase900,
    surface = ChirpBase0,
    onSurface = ChirpBase900,
    surfaceVariant = ChirpBase100,
    onSurfaceVariant = ChirpBase700,

    outline = ChirpBase200,
    outlineVariant = ChirpBase100,
)

val DarkColorScheme = darkColorScheme(
    primary = ChirpBrand500,
    onPrimary = ChirpBase0,
    primaryContainer = ChirpBrand900,
    onPrimaryContainer = ChirpBrand100,

    secondary = ChirpBase400D,
    onSecondary = ChirpBase1000,
    secondaryContainer = ChirpBase900D,
    onSecondaryContainer = ChirpBase400D,

    tertiary = ChirpBrand600,
    onTertiary = ChirpBase0,
    tertiaryContainer = ChirpBrand900,
    onTertiaryContainer = ChirpBrand100,

    error = ChirpRed500,
    onError = ChirpBase0,
    errorContainer = ChirpRed600,
    onErrorContainer = ChirpRed200,

    background = ChirpBase1000,
    onBackground = ChirpBase0,
    surface = ChirpBase950,
    onSurface = ChirpBase0,
    surfaceVariant = ChirpBase900D,
    onSurfaceVariant = ChirpBase400D,

    outline = ChirpBase900D,
    outlineVariant = ChirpBase950,
)
