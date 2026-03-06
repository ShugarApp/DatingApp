package com.dating.core.presentation.permissions

import dev.icerock.moko.permissions.PermissionDelegate

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect val locationPermissionDelegate: PermissionDelegate

internal object LocationPermission : dev.icerock.moko.permissions.Permission {
    override val delegate get() = locationPermissionDelegate
}

internal val dev.icerock.moko.permissions.Permission.Companion.LOCATION get() = LocationPermission
