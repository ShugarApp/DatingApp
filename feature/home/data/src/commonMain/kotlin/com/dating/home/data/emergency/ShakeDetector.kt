package com.dating.home.data.emergency

expect class ShakeDetector() {
    fun start(onShake: () -> Unit)
    fun stop()
}
