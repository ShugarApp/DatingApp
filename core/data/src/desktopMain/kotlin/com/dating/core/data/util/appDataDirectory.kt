package com.dating.core.data.util

import java.io.File

val appDataDirectory: File
    get() {
        val userHome = System.getProperty("user.home")
        return when (currentOs) {
            DesktopOs.WINDOWS -> File(System.getenv("APPDATA"), "Aura")
            DesktopOs.MACOS -> File(userHome, "Library/Application Support/Aura")
            DesktopOs.LINUX -> File(userHome, ".local/share/Aura")
        }
    }
