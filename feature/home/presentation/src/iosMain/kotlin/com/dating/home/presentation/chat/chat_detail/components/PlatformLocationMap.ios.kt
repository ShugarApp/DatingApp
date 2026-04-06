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

    SideEffect {
        mapDelegate.onRegionWillChange = { onMovingChanged(true) }
        mapDelegate.onRegionDidChange = { lat, lng ->
            onMovingChanged(false)
            val clLocation = CLLocation(latitude = lat, longitude = lng)
            CLGeocoder().reverseGeocodeLocation(clLocation) { placemarks, _ ->
                @Suppress("UNCHECKED_CAST")
                val placemark = (placemarks as? List<platform.CoreLocation.CLPlacemark>)?.firstOrNull()
                val name = placemark?.name ?: placemark?.thoroughfare ?: "Selected location"
                val addressParts = listOfNotNull(
                    placemark?.thoroughfare,
                    placemark?.locality,
                    placemark?.administrativeArea
                )
                val address = addressParts.joinToString(", ")
                    .ifBlank { "%.5f, %.5f".format(lat, lng) }
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

    UIKitView(
        factory = {
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
