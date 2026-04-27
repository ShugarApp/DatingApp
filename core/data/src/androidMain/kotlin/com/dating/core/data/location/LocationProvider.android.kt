package com.dating.core.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import com.dating.core.domain.location.LatLng
import com.dating.core.domain.location.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): LatLng? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

        // Try cached location first (fast path)
        for (provider in providers) {
            val location = try {
                locationManager.getLastKnownLocation(provider)
            } catch (_: Exception) {
                null
            }
            if (location != null) {
                return LatLng(location.latitude, location.longitude)
            }
        }

        // No cached location — request a fresh one with a 10s timeout
        return withTimeoutOrNull(10_000L) {
            requestFreshLocation(locationManager, providers)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestFreshLocation(
        locationManager: LocationManager,
        providers: List<String>
    ): LatLng? = suspendCancellableCoroutine { continuation ->
        val enabledProvider = providers.firstOrNull { locationManager.isProviderEnabled(it) }
            ?: run {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationManager.removeUpdates(this)
                if (!continuation.isCompleted) continuation.resume(LatLng(location.latitude, location.longitude))
            }

            override fun onProviderDisabled(provider: String) {
                locationManager.removeUpdates(this)
                if (!continuation.isCompleted) continuation.resume(null)
            }
        }

        try {
            @Suppress("DEPRECATION")
            locationManager.requestSingleUpdate(enabledProvider, listener, Looper.getMainLooper())
        } catch (_: Exception) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        continuation.invokeOnCancellation { locationManager.removeUpdates(listener) }
    }
}
