package com.dating.core.data.util

import java.io.File

val appDataDirectory: File
    get() {
        val userHome = System.getProperty("user.home")
        return when (currentOs) {
            DesktopOs.WINDOWS -> File(System.getenv("APPDATA"), "Shugar")
            DesktopOs.MACOS -> File(userHome, "Library/Application Support/Shugar")
            DesktopOs.LINUX -> File(userHome, ".local/share/Shugar")
        }
    }
