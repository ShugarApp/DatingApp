package com.dating.core.presentation.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberOpenUrl(): (String) -> Unit

@Composable
expect fun rememberOpenNotificationSettings(): () -> Unit
