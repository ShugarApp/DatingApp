package app.shugar.shugar.windows

import java.util.UUID

data class WindowState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Shugar",
    val isFocused: Boolean = false
)
