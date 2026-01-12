package com.dating.aura

import com.dating.home.data.notification.IosDeviceTokenHolder

object IosDeviceTokenHolderBridge {
    fun updateToken(token: String) {
        IosDeviceTokenHolder.updateToken(token)
    }
}