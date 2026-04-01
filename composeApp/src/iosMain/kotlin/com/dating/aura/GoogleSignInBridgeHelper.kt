package com.dating.aura

import com.dating.core.domain.auth.GoogleSignInBridge

/**
 * Thin wrapper so Swift can access GoogleSignInBridge
 * (Swift sees this as GoogleSignInBridgeHelper from ComposeApp module).
 */
object GoogleSignInBridgeHelper {
    fun setSignInHandler(handler: () -> Unit) {
        GoogleSignInBridge.setSignInHandler(handler)
    }

    fun onSignInComplete(idToken: String?) {
        GoogleSignInBridge.onSignInComplete(idToken)
    }
}
