package com.dating.core.presentation.permissions

import android.Manifest
import android.content.Context
import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.PermissionState

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual val locationPermissionDelegate: PermissionDelegate = AndroidLocationPermissionDelegate

private object AndroidLocationPermissionDelegate : PermissionDelegate {
    override fun getPermissionStateOverride(applicationContext: Context): PermissionState? = null
    override fun getPlatformPermission(): List<String> =
        listOf(Manifest.permission.ACCESS_FINE_LOCATION)
}
