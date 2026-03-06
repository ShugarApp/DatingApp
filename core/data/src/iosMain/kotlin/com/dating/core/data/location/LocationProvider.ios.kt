package com.dating.core.data.location

import com.dating.core.domain.location.LatLng
import com.dating.core.domain.location.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyBest
import kotlin.coroutines.resume

class IosLocationProvider : LocationProvider {

    override suspend fun getLastKnownLocation(): LatLng? {
        return suspendCancellableCoroutine { continuation ->
            val manager = CLLocationManager()
            val status = CLLocationManager.authorizationStatus()
            if (status != kCLAuthorizationStatusAuthorizedWhenInUse &&
                status != kCLAuthorizationStatusAuthorizedAlways
            ) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }
            manager.desiredAccuracy = kCLLocationAccuracyBest
            val location = manager.location
            if (location != null) {
                continuation.resume(
                    LatLng(
                        latitude = location.coordinate.useContents { latitude },
                        longitude = location.coordinate.useContents { longitude }
                    )
                )
            } else {
                continuation.resume(null)
            }
        }
    }
}
