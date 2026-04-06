package com.dating.home.presentation.chat.chat_detail.components

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.dating.home.domain.models.DateProposalLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import androidx.compose.runtime.snapshotFlow

@Composable
actual fun PlatformLocationMap(
    onLocationChanged: (DateProposalLocation) -> Unit,
    onMovingChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.7128, -74.006), 14f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            scope.launch {
                try {
                    val location = LocationServices
                        .getFusedLocationProviderClient(context)
                        .lastLocation
                        .await()
                    location?.let {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                        )
                    }
                } catch (_: Exception) { }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            try {
                val location = LocationServices
                    .getFusedLocationProviderClient(context)
                    .lastLocation
                    .await()
                location?.let {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            } catch (_: Exception) { }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Forward moving state — uses a persistent flow, never cancelled by map movement
    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .collect { isMoving -> onMovingChanged(isMoving) }
    }

    // Emit location as soon as the camera settles; then update with geocoded name
    LaunchedEffect(Unit) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .filter { !it }
            .collectLatest {
                val target = cameraPositionState.position.target
                // Immediate coordinate-based location so the button is never blocked
                onLocationChanged(
                    DateProposalLocation(
                        name = "Selected location",
                        address = "%.5f, %.5f".format(target.latitude, target.longitude),
                        latitude = target.latitude,
                        longitude = target.longitude
                    )
                )
                // Geocode in the background and update if successful
                withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = Geocoder(context, Locale.getDefault())
                            .getFromLocation(target.latitude, target.longitude, 1)
                        val address = addresses?.firstOrNull() ?: return@withContext
                        val name = address.featureName
                            ?: address.thoroughfare
                            ?: address.subLocality
                            ?: return@withContext
                        val fullAddress = address.getAddressLine(0) ?: return@withContext
                        withContext(Dispatchers.Main) {
                            onLocationChanged(
                                DateProposalLocation(
                                    name = name,
                                    address = fullAddress,
                                    latitude = target.latitude,
                                    longitude = target.longitude
                                )
                            )
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                    }
                }
            }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = hasPermission,
            compassEnabled = false,
            mapToolbarEnabled = false,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false
        ),
        properties = MapProperties(
            isMyLocationEnabled = hasPermission
        )
    )
}
