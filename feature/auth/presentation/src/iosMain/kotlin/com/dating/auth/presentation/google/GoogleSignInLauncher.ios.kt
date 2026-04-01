package com.dating.auth.presentation.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dating.core.domain.auth.GoogleSignInBridge
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: () -> Unit
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()

    return remember {
        {
            coroutineScope.launch {
                val idToken = suspendCoroutine<String?> { continuation ->
                    GoogleSignInBridge.requestSignIn { token ->
                        continuation.resume(token)
                    }
                }

                if (idToken != null) {
                    onIdTokenReceived(idToken)
                } else {
                    onError()
                }
            }
        }
    }
}
