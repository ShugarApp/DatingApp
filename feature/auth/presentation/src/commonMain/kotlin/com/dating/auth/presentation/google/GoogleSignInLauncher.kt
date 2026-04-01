package com.dating.auth.presentation.google

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: () -> Unit
): () -> Unit
