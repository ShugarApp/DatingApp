package com.dating.auth.presentation.google

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
actual fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: () -> Unit
): () -> Unit {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    return remember(activity) {
        {
            coroutineScope.launch {
                try {
                    val credentialManager = CredentialManager.create(activity)

                    val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                        serverClientId = GoogleClientIds.WEB_CLIENT_ID
                    ).build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(signInWithGoogleOption)
                        .build()

                    val result = credentialManager.getCredential(
                        context = activity,
                        request = request
                    )

                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
                        result.credential.data
                    )

                    onIdTokenReceived(googleIdTokenCredential.idToken)
                } catch (_: GetCredentialCancellationException) {
                    // User cancelled, do nothing
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Error: ${e.message}", e)
                    onError()
                }
            }
        }
    }
}
