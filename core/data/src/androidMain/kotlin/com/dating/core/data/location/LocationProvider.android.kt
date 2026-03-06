package com.dating.core.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.dating.core.domain.location.LatLng
import com.dating.core.domain.location.LocationProvider

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): LatLng? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
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
        return null
    }
}
