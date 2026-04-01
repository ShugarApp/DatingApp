package com.dating.core.domain.auth

object GoogleSignInBridge {

    private var onSignInRequest: (() -> Unit)? = null
    private var onResult: ((String?) -> Unit)? = null

    /**
     * Called from the platform layer (e.g., Swift) to register the sign-in handler.
     */
    fun setSignInHandler(handler: () -> Unit) {
        onSignInRequest = handler
    }

    /**
     * Called from the presentation layer to trigger sign-in.
     */
    fun requestSignIn(resultCallback: (String?) -> Unit) {
        onResult = resultCallback
        onSignInRequest?.invoke() ?: resultCallback(null)
    }

    /**
     * Called from the platform layer when sign-in completes.
     */
    fun onSignInComplete(idToken: String?) {
        onResult?.invoke(idToken)
        onResult = null
    }
}
