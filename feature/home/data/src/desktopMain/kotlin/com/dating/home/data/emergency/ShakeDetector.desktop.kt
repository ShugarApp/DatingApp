package com.dating.home.data.emergency

actual class ShakeDetector actual constructor() {
    actual fun start(onShake: () -> Unit) {
        // Shake detection not supported on desktop
    }

    actual fun stop() {
        // No-op
    }
}
