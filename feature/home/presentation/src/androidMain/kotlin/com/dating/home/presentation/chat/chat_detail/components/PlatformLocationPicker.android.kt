package com.dating.home.presentation.chat.chat_detail.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.dating.home.domain.models.DateProposalLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

private var sharedPlacesClient: com.google.android.libraries.places.api.net.PlacesClient? = null

private fun getOrCreatePlacesClient(context: android.content.Context): com.google.android.libraries.places.api.net.PlacesClient {
    return sharedPlacesClient ?: Places.createClient(context.applicationContext).also {
        sharedPlacesClient = it
    }
}

private enum class LocationCategory(
    val label: String,
    val placeType: String,
    val icon: ImageVector
) {
    RESTAURANTS("Restaurants", "restaurant", Icons.Default.Restaurant),
    CAFES("Cafés", "cafe", Icons.Default.LocalCafe),
    PARKS("Parks", "park", Icons.Default.Park),
    BARS("Bars", "bar", Icons.Default.LocalBar),
    MUSEUMS("Museums", "museum", Icons.Default.Museum),
    CINEMAS("Cinemas", "movie_theater", Icons.Default.Movie)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformLocationPicker(
    initialLocation: DateProposalLocation?,
    onLocationSelected: (DateProposalLocation) -> Unit,
    onDismiss: () -> Unit
) {
    if (!Places.isInitialized()) {
        LocationPickerFallback(
            initialLocation = initialLocation,
            onLocationSelected = onLocationSelected,
            onDismiss = onDismiss
        )
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val placesClient = remember { getOrCreatePlacesClient(context) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var nearbyPlaces by remember { mutableStateOf<List<DateProposalLocation>>(emptyList()) }
    var placePhotos by remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    var pinLatLng by remember { mutableStateOf(initialLocation?.let { LatLng(it.latitude, it.longitude) }) }
    var isLoadingNearby by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<LocationCategory?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(40.7128, -74.0060),
            14f
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        currentLatLng = latLng
                        pinLatLng = latLng
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    currentLatLng = latLng
                    if (initialLocation == null) {
                        pinLatLng = latLng
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            } catch (_: Exception) {}
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length < 2) {
            searchResults = emptyList()
            isSearchLoading = false
            return@LaunchedEffect
        }
        isSearchLoading = true
        try {
            val bias = currentLatLng?.let { latLng ->
                val delta = 0.45 // ~50 km
                RectangularBounds.newInstance(
                    LatLng(latLng.latitude - delta, latLng.longitude - delta),
                    LatLng(latLng.latitude + delta, latLng.longitude + delta)
                )
            }
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchQuery)
                .apply { bias?.let { setLocationBias(it) } }
                .build()
            val response = placesClient.findAutocompletePredictions(request).await()
            searchResults = response.autocompletePredictions
        } catch (_: Exception) {
            searchResults = emptyList()
        }
        isSearchLoading = false
    }

    LaunchedEffect(selectedCategory, currentLatLng) {
        val cat = selectedCategory ?: return@LaunchedEffect
        val center = currentLatLng ?: return@LaunchedEffect
        isLoadingNearby = true
        nearbyPlaces = emptyList()
        placePhotos = emptyMap()
        try {
            val request = SearchNearbyRequest.builder(
                CircularBounds.newInstance(center, 1500.0),
                listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.PHOTO_METADATAS
                )
            ).setIncludedTypes(listOf(cat.placeType))
                .setMaxResultCount(10)
                .build()

            val response = placesClient.searchNearby(request).await()

            nearbyPlaces = response.places.mapNotNull { place ->
                val latLng = place.latLng ?: return@mapNotNull null
                DateProposalLocation(
                    name = place.name ?: return@mapNotNull null,
                    address = place.address ?: "",
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    placeId = place.id
                )
            }

            // Load photos asynchronously
            response.places.forEach { place ->
                val placeId = place.id ?: return@forEach
                val photoMeta = place.photoMetadatas?.firstOrNull() ?: return@forEach
                scope.launch {
                    try {
                        val photoRequest = FetchPhotoRequest.builder(photoMeta)
                            .setMaxWidth(160)
                            .setMaxHeight(160)
                            .build()
                        val photoResponse = placesClient.fetchPhoto(photoRequest).await()
                        placePhotos = placePhotos + (placeId to photoResponse.bitmap)
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {
            nearbyPlaces = emptyList()
        }
        isLoadingNearby = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Choose location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (selectedLocation != null) {
                            Button(
                                onClick = { onLocationSelected(selectedLocation!!) },
                                modifier = Modifier.padding(end = 12.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Confirm", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search a place...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        if (isSearchLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                if (searchQuery.isNotBlank()) {
                    // Autocomplete results
                    if (!isSearchLoading && searchResults.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No results for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            items(searchResults) { prediction ->
                                SearchResultItem(
                                    prediction = prediction,
                                    onClick = {
                                        val name = prediction.getPrimaryText(null).toString()
                                        val address = prediction.getSecondaryText(null).toString()
                                        // Immediately close search and show selected location
                                        selectedLocation = DateProposalLocation(
                                            name = name,
                                            address = address,
                                            latitude = 0.0,
                                            longitude = 0.0
                                        )
                                        searchQuery = ""
                                        // Fetch coordinates in background
                                        scope.launch {
                                            try {
                                                val fields = listOf(
                                                    Place.Field.NAME,
                                                    Place.Field.ADDRESS,
                                                    Place.Field.LAT_LNG,
                                                    Place.Field.ID
                                                )
                                                val request = FetchPlaceRequest.newInstance(prediction.placeId, fields)
                                                val place = placesClient.fetchPlace(request).await().place
                                                val latLng = place.latLng ?: return@launch
                                                selectedLocation = DateProposalLocation(
                                                    name = place.name ?: name,
                                                    address = place.address ?: address,
                                                    latitude = latLng.latitude,
                                                    longitude = latLng.longitude,
                                                    placeId = place.id
                                                )
                                                pinLatLng = latLng
                                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                                            } catch (_: Exception) {
                                                // FetchPlace failed — try Geocoder as fallback
                                                try {
                                                    @Suppress("DEPRECATION")
                                                    val addresses = Geocoder(context, Locale.getDefault())
                                                        .getFromLocationName("$name $address", 1)
                                                    val result = addresses?.firstOrNull() ?: return@launch
                                                    val latLng = LatLng(result.latitude, result.longitude)
                                                    selectedLocation = selectedLocation?.copy(
                                                        latitude = latLng.latitude,
                                                        longitude = latLng.longitude
                                                    )
                                                    pinLatLng = latLng
                                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                                                } catch (_: Exception) {}
                                            }
                                        }
                                    }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                } else {
                    // Map
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                scope.launch {
                                    pinLatLng = latLng
                                    selectedCategory = null
                                    try {
                                        @Suppress("DEPRECATION")
                                        val addresses = Geocoder(context, Locale.getDefault())
                                            .getFromLocation(latLng.latitude, latLng.longitude, 1)
                                        val address = addresses?.firstOrNull()
                                        selectedLocation = DateProposalLocation(
                                            name = address?.featureName ?: "Selected location",
                                            address = address?.getAddressLine(0) ?: "",
                                            latitude = latLng.latitude,
                                            longitude = latLng.longitude
                                        )
                                    } catch (_: Exception) {
                                        selectedLocation = DateProposalLocation(
                                            name = "Selected location",
                                            address = "%.5f, %.5f".format(latLng.latitude, latLng.longitude),
                                            latitude = latLng.latitude,
                                            longitude = latLng.longitude
                                        )
                                    }
                                }
                            }
                        ) {
                            pinLatLng?.let {
                                Marker(
                                    state = MarkerState(position = it),
                                    title = selectedLocation?.name
                                )
                            }
                        }

                        FloatingActionButton(
                            onClick = {
                                currentLatLng?.let { latLng ->
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                                        )
                                    }
                                } ?: locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            elevation = FloatingActionButtonDefaults.elevation(4.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = "My location")
                        }
                    }

                    // Bottom panel
                    Surface(
                        tonalElevation = 3.dp,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Category chips
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(LocationCategory.entries) { category ->
                                    FilterChip(
                                        selected = selectedCategory == category,
                                        onClick = {
                                            selectedCategory = if (selectedCategory == category) null else category
                                            nearbyPlaces = emptyList()
                                            placePhotos = emptyMap()
                                        },
                                        label = { Text(category.label) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (selectedCategory == category) Icons.Default.Check else category.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }

                            when {
                                selectedCategory != null -> {
                                    if (isLoadingNearby) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else if (nearbyPlaces.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No ${selectedCategory!!.label.lowercase()} found nearby",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 260.dp),
                                            contentPadding = PaddingValues(bottom = 8.dp)
                                        ) {
                                            items(nearbyPlaces) { place ->
                                                val isSelected = selectedLocation?.latitude == place.latitude &&
                                                    selectedLocation?.longitude == place.longitude
                                                NearbyPlaceItem(
                                                    place = place,
                                                    isSelected = isSelected,
                                                    photo = place.placeId?.let { placePhotos[it] },
                                                    fallbackIcon = selectedCategory!!.icon,
                                                    onClick = {
                                                        selectedLocation = place
                                                        pinLatLng = LatLng(place.latitude, place.longitude)
                                                        scope.launch {
                                                            cameraPositionState.animate(
                                                                CameraUpdateFactory.newLatLngZoom(
                                                                    LatLng(place.latitude, place.longitude), 16f
                                                                )
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                selectedLocation != null -> {
                                    SelectedLocationCard(
                                        location = selectedLocation!!,
                                        onConfirm = { onLocationSelected(selectedLocation!!) },
                                        onClear = {
                                            selectedLocation = null
                                            pinLatLng = null
                                        }
                                    )
                                }

                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = "Tap anywhere on the map to drop a pin, or pick a category above",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prediction.getPrimaryText(null).toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = prediction.getSecondaryText(null).toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NearbyPlaceItem(
    place: DateProposalLocation,
    isSelected: Boolean,
    photo: Bitmap?,
    fallbackIcon: ImageVector,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Photo or icon
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = fallbackIcon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (place.address.isNotBlank()) {
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SelectedLocationCard(
    location: DateProposalLocation,
    onConfirm: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (location.address.isNotBlank()) {
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        TextButton(onClick = onClear) {
            Text("Clear", color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Button(
            onClick = onConfirm,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Select")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerFallback(
    initialLocation: DateProposalLocation?,
    onLocationSelected: (DateProposalLocation) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialLocation?.name ?: "") }
    var address by remember { mutableStateOf(initialLocation?.address ?: "") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Choose location",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Place name") },
                    placeholder = { Text("e.g. Central Park") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address (optional)") },
                    placeholder = { Text("e.g. New York, NY") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onLocationSelected(
                                DateProposalLocation(
                                    name = name,
                                    address = address,
                                    latitude = 0.0,
                                    longitude = 0.0
                                )
                            )
                        },
                        enabled = name.isNotBlank()
                    ) { Text("Confirm") }
                }
            }
        }
    }
}
