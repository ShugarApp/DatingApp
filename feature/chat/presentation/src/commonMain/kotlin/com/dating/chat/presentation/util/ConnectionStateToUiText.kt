package com.dating.chat.presentation.util

import aura.feature.chat.presentation.generated.resources.Res
import aura.feature.chat.presentation.generated.resources.network_error
import aura.feature.chat.presentation.generated.resources.offline
import aura.feature.chat.presentation.generated.resources.online
import aura.feature.chat.presentation.generated.resources.reconnecting
import aura.feature.chat.presentation.generated.resources.unknown_error
import com.dating.chat.domain.models.ConnectionState
import com.dating.core.presentation.util.UiText

fun ConnectionState.toUiText(): UiText {
    val resource = when(this) {
        ConnectionState.DISCONNECTED -> Res.string.offline
        ConnectionState.CONNECTING -> Res.string.reconnecting
        ConnectionState.CONNECTED -> Res.string.online
        ConnectionState.ERROR_NETWORK -> Res.string.network_error
        ConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }
    return UiText.Resource(resource)
}