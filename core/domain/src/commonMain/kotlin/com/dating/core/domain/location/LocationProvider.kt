package com.dating.core.domain.location

data class LatLng(val latitude: Double, val longitude: Double)

interface LocationProvider {
    suspend fun getLastKnownLocation(): LatLng?
}
