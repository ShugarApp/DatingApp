package com.dating.home.data.emergency

actual class ShakeDetector actual constructor() {
    private var onShakeCallback: (() -> Unit)? = null

    actual fun start(onShake: () -> Unit) {
        onShakeCallback = onShake
        ShakeDetectorRegistry.register(onShake)
    }

    actual fun stop() {
        onShakeCallback = null
        ShakeDetectorRegistry.unregister()
    }
}

object ShakeDetectorRegistry {
    private var callback: (() -> Unit)? = null

    fun register(callback: () -> Unit) {
        this.callback = callback
    }

    fun unregister() {
        callback = null
    }

    fun onShakeDetected() {
        callback?.invoke()
    }
}
