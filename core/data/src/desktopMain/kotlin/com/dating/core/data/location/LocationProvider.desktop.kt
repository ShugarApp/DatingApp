package com.dating.core.data.location

import com.dating.core.domain.location.LatLng
import com.dating.core.domain.location.LocationProvider

class DesktopLocationProvider : LocationProvider {
    override suspend fun getLastKnownLocation(): LatLng? = null
}
