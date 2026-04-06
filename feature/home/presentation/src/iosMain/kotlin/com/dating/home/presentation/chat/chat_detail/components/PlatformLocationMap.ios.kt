package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.dating.home.domain.models.DateProposalLocation
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
private class MapRegionDelegate : NSObject(), MKMapViewDelegateProtocol {
    var onRegionWillChange: (() -> Unit)? = null
    var onRegionDidChange: ((Double, Double) -> Unit)? = null

    override fun mapView(mapView: MKMapView, regionWillChangeAnimated: Boolean) {
        onRegionWillChange?.invoke()
    }

    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        mapView.region.center.useContents { onRegionDidChange?.invoke(latitude, longitude) }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class InitialLocationDelegate(
    private val onFirstLocation: (Double, Double) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {
    private var fired = false

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        if (fired) return
        @Suppress("UNCHECKED_CAST")
        val loc = (didUpdateLocations as? List<CLLocation>)?.lastOrNull() ?: return
        fired = true
        loc.coordinate.useContents { onFirstLocation(latitude, longitude) }
        manager.stopUpdatingLocation()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformLocationMap(
    onLocationChanged: (DateProposalLocation) -> Unit,
    onMovingChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    val mapDelegate = remember { MapRegionDelegate() }
    val locationManager = remember { CLLocationManager() }
    val geocoder = remember { CLGeocoder() }
    var mapViewRef by remember { mutableStateOf<MKMapView?>(null) }

    val locationDelegate = remember {
        InitialLocationDelegate { lat, lng ->
            val coord = CLLocationCoordinate2DMake(lat, lng)
            mapViewRef?.setRegion(
                MKCoordinateRegionMakeWithDistance(coord, 1000.0, 1000.0),
                animated = true
            )
        }
    }

    LaunchedEffect(Unit) {
        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()

        // Center immediately if last location is already known
        locationManager.location?.let { loc ->
            val coord = loc.coordinate
            coord.useContents {
                mapViewRef?.setRegion(
                    MKCoordinateRegionMakeWithDistance(
                        CLLocationCoordinate2DMake(latitude, longitude),
                        1000.0, 1000.0
                    ),
                    animated = false
                )
            }
        } ?: locationManager.startUpdatingLocation()
    }

    // Build the regionDidChange handler as a lambda so it can be assigned in
    // both factory (before the first SideEffect) and SideEffect (kept fresh).
    val makeRegionDidChange: (lat: Double, lng: Double) -> Unit = { lat, lng ->
        onMovingChanged(false)
        // Emit coordinates immediately — button is never blocked
        onLocationChanged(
            DateProposalLocation(
                name = "Selected location",
                address = "%.5f, %.5f".format(lat, lng),
                latitude = lat,
                longitude = lng
            )
        )
        // Geocode and update with proper name if successful
        val clLocation = CLLocation(latitude = lat, longitude = lng)
        geocoder.cancelGeocode()
        geocoder.reverseGeocodeLocation(clLocation) { placemarks, _ ->
            @Suppress("UNCHECKED_CAST")
            val placemark = (placemarks as? List<platform.CoreLocation.CLPlacemark>)?.firstOrNull()
            val name = placemark?.name ?: placemark?.thoroughfare
            if (placemark != null && name != null) {
                val addressParts = listOfNotNull(
                    placemark.thoroughfare,
                    placemark.locality,
                    placemark.administrativeArea
                )
                val address = addressParts.joinToString(", ").ifBlank { "%.5f, %.5f".format(lat, lng) }
                onLocationChanged(
                    DateProposalLocation(
                        name = name,
                        address = address,
                        latitude = lat,
                        longitude = lng
                    )
                )
            }
        }
    }

    // Keep callbacks fresh on every recomposition
    SideEffect {
        mapDelegate.onRegionWillChange = { onMovingChanged(true) }
        mapDelegate.onRegionDidChange = makeRegionDidChange
    }

    UIKitView(
        factory = {
            // Also set here: factory runs synchronously during composition, before the
            // first SideEffect. The map can fire regionDidChangeAnimated on the next
            // runloop tick — if callbacks aren't set yet they'd be missed.
            mapDelegate.onRegionWillChange = { onMovingChanged(true) }
            mapDelegate.onRegionDidChange = makeRegionDidChange
            MKMapView().also { map ->
                mapViewRef = map
                map.setShowsUserLocation(true)
                map.delegate = mapDelegate
                locationManager.location?.let { loc ->
                    val coord = loc.coordinate
                    map.setRegion(
                        MKCoordinateRegionMakeWithDistance(coord, 1000.0, 1000.0),
                        animated = false
                    )
                }
            }
        },
        update = { map ->
            map.delegate = mapDelegate
        },
        modifier = modifier
    )
}
