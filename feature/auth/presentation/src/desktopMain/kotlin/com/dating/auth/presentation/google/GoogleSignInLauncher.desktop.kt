package com.dating.auth.presentation.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: () -> Unit
): () -> Unit {
    // Google Sign-In is not supported on Desktop
    return remember { { onError() } }
}
