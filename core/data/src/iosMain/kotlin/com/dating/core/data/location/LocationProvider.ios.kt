package com.dating.core.data.location

import com.dating.core.domain.location.LatLng
import com.dating.core.domain.location.LocationProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyBest
import kotlin.coroutines.resume

class IosLocationProvider : LocationProvider {

    @OptIn(ExperimentalForeignApi::class)
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
                val latLng = location.coordinate.useContents {
                    LatLng(latitude = latitude, longitude = longitude)
                }
                continuation.resume(latLng)
            } else {
                continuation.resume(null)
            }
        }
    }
}
