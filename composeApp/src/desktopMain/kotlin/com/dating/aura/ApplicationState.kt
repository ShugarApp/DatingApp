package com.dating.aura

import androidx.compose.ui.window.TrayState
import com.dating.aura.windows.WindowState
import com.dating.core.domain.preferences.ThemePreference

data class ApplicationState(
    val windows: List<WindowState> = listOf(WindowState()),
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val trayState: TrayState = TrayState()
)
