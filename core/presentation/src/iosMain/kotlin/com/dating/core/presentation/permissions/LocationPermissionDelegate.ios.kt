package com.dating.core.presentation.permissions

import dev.icerock.moko.permissions.PermissionDelegate
import dev.icerock.moko.permissions.PermissionState
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import kotlin.coroutines.resume

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual val locationPermissionDelegate: PermissionDelegate = IosLocationPermissionDelegate

private object IosLocationPermissionDelegate : PermissionDelegate {
    override suspend fun providePermission() = suspendCancellableCoroutine { continuation ->
        CLLocationManager().requestWhenInUseAuthorization()
        continuation.resume(Unit)
    }

    override suspend fun getPermissionState(): PermissionState {
        return when (CLLocationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> PermissionState.Granted
            kCLAuthorizationStatusDenied -> PermissionState.DeniedAlways
            else -> PermissionState.NotDetermined
        }
    }
}
