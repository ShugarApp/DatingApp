package com.dating.aura.windows

import java.util.UUID

data class WindowState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Aura",
    val isFocused: Boolean = false
)
