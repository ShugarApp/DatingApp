package app.shugar.shugar

import androidx.compose.ui.window.TrayState
import app.shugar.shugar.windows.WindowState
import com.dating.core.domain.preferences.ThemePreference

data class ApplicationState(
    val windows: List<WindowState> = listOf(WindowState()),
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val trayState: TrayState = TrayState()
)
