package com.dating.home.domain.emergency

interface ShakeDetector {
    fun start(onShake: () -> Unit)
    fun stop()
}
