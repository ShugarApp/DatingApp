package com.dating.home.presentation.chat.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dating.home.domain.models.DateProposalLocation
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKLocalSearch
import platform.MapKit.MKLocalSearchRequest
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UIGestureRecognizer
import platform.UIKit.UIGestureRecognizerStateBegan
import platform.UIKit.UIGestureRecognizerStateEnded
import platform.UIKit.UILongPressGestureRecognizer
import platform.UIKit.UITapGestureRecognizer
import platform.darwin.NSObject

private enum class LocationCategory(
    val label: String,
    val naturalLanguageQuery: String,
    val icon: ImageVector
) {
    RESTAURANTS("Restaurants", "restaurant", Icons.Default.Restaurant),
    CAFES("Cafés", "cafe", Icons.Default.LocalCafe),
    PARKS("Parks", "park", Icons.Default.Park),
    BARS("Bars", "bar", Icons.Default.LocalBar),
    MUSEUMS("Museums", "museum", Icons.Default.Museum),
    CINEMAS("Cinemas", "cinema", Icons.Default.Movie)
}

// NSObject subclass to handle map tap via UIKit target-action
private class MapTapHandler : NSObject() {
    var onTap: ((Double, Double) -> Unit)? = null

    @Suppress("unused")
    fun handleTap(sender: UIGestureRecognizer) {
        val mapView = sender.view as? MKMapView ?: return
        val point = sender.locationInView(mapView)
        val coord = mapView.convertPoint(point, toCoordinateFromView = mapView)
        coord.useContents { onTap?.invoke(latitude, longitude) }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalForeignApi::class)
@Composable
actual fun PlatformLocationPicker(
    initialLocation: DateProposalLocation?,
    onLocationSelected: (DateProposalLocation) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val locationManager = remember { CLLocationManager() }
    val tapHandler = remember { MapTapHandler() }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<DateProposalLocation>>(emptyList()) }
    var nearbyPlaces by remember { mutableStateOf<List<DateProposalLocation>>(emptyList()) }
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var isLoadingNearby by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<LocationCategory?>(null) }
    var mapView by remember { mutableStateOf<MKMapView?>(null) }
    var currentCoordinate by remember {
        mutableStateOf(
            initialLocation?.let { CLLocationCoordinate2DMake(it.latitude, it.longitude) }
        )
    }

    LaunchedEffect(Unit) {
        locationManager.requestWhenInUseAuthorization()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }

    // Keep tap handler callback up to date with latest state setters
    SideEffect {
        tapHandler.onTap = { lat, lng ->
            val coord = CLLocationCoordinate2DMake(lat, lng)
            currentCoordinate = coord
            selectedCategory = null

            // Update pin on map
            mapView?.removeAnnotations(mapView!!.annotations)
            val annotation = MKPointAnnotation()
            annotation.setCoordinate(coord)
            mapView?.addAnnotation(annotation)
            mapView?.setRegion(MKCoordinateRegionMakeWithDistance(coord, 600.0, 600.0), animated = true)

            // Reverse geocode
            val location = CLLocation(latitude = lat, longitude = lng)
            CLGeocoder().reverseGeocodeLocation(location) { placemarks, _ ->
                @Suppress("UNCHECKED_CAST")
                val placemark = (placemarks as? List<platform.CoreLocation.CLPlacemark>)?.firstOrNull()
                val name = placemark?.name ?: "Selected location"
                val address = listOfNotNull(placemark?.thoroughfare, placemark?.locality).joinToString(", ")
                selectedLocation = DateProposalLocation(
                    name = name,
                    address = address,
                    latitude = lat,
                    longitude = lng
                )
                annotation.setTitle(name)
            }
        }
    }

    // Search by text
    LaunchedEffect(searchQuery) {
        if (searchQuery.length < 2) {
            searchResults = emptyList()
            isSearchLoading = false
            return@LaunchedEffect
        }
        isSearchLoading = true
        val request = MKLocalSearchRequest()
        request.setNaturalLanguageQuery(searchQuery)
        currentCoordinate?.let { coord ->
            request.setRegion(MKCoordinateRegionMakeWithDistance(coord, 50_000.0, 50_000.0))
        }
        val search = MKLocalSearch(request)
        search.startWithCompletionHandler { response, _ ->
            searchResults = response?.mapItems?.mapNotNull { item ->
                val coord = item.placemark.coordinate
                DateProposalLocation(
                    name = item.name ?: return@mapNotNull null,
                    address = listOfNotNull(item.placemark.thoroughfare, item.placemark.locality).joinToString(", "),
                    latitude = coord.useContents { latitude },
                    longitude = coord.useContents { longitude }
                )
            } ?: emptyList()
            isSearchLoading = false
        }
    }

    // Nearby places by category
    LaunchedEffect(selectedCategory, currentCoordinate) {
        val cat = selectedCategory ?: return@LaunchedEffect
        val coord = currentCoordinate ?: return@LaunchedEffect
        isLoadingNearby = true
        nearbyPlaces = emptyList()
        val request = MKLocalSearchRequest()
        request.setNaturalLanguageQuery(cat.naturalLanguageQuery)
        request.setRegion(MKCoordinateRegionMakeWithDistance(coord, 1500.0, 1500.0))
        val search = MKLocalSearch(request)
        search.startWithCompletionHandler { response, _ ->
            nearbyPlaces = response?.mapItems?.take(10)?.mapNotNull { item ->
                val c = item.placemark.coordinate
                DateProposalLocation(
                    name = item.name ?: return@mapNotNull null,
                    address = listOfNotNull(item.placemark.thoroughfare, item.placemark.locality).joinToString(", "),
                    latitude = c.useContents { latitude },
                    longitude = c.useContents { longitude }
                )
            } ?: emptyList()
            isLoadingNearby = false
        }
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
                    // Search results
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
                            items(searchResults) { place ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedLocation = place
                                            searchQuery = ""
                                            val coord = CLLocationCoordinate2DMake(place.latitude, place.longitude)
                                            mapView?.setRegion(
                                                MKCoordinateRegionMakeWithDistance(coord, 600.0, 600.0),
                                                animated = true
                                            )
                                            mapView?.removeAnnotations(mapView!!.annotations)
                                            val annotation = MKPointAnnotation()
                                            annotation.setCoordinate(coord)
                                            annotation.setTitle(place.name)
                                            mapView?.addAnnotation(annotation)
                                        }
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
                                            text = place.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (place.address.isNotBlank()) {
                                            Text(
                                                text = place.address,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }
                } else {
                    // Map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        UIKitView(
                            factory = {
                                MKMapView().also { map ->
                                    mapView = map
                                    map.setShowsUserLocation(true)
                                    initialLocation?.let { loc ->
                                        val coord = CLLocationCoordinate2DMake(loc.latitude, loc.longitude)
                                        map.setRegion(
                                            MKCoordinateRegionMakeWithDistance(coord, 1000.0, 1000.0),
                                            animated = false
                                        )
                                        val annotation = MKPointAnnotation()
                                        annotation.setCoordinate(coord)
                                        annotation.setTitle(loc.name)
                                        map.addAnnotation(annotation)
                                    }
                                    // Single tap gesture
                                    val tap = UITapGestureRecognizer(
                                        target = tapHandler,
                                        action = platform.objc.sel_registerName("handleTap:")
                                    )
                                    map.addGestureRecognizer(tap)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        FloatingActionButton(
                            onClick = {
                                locationManager.location?.let { loc ->
                                    val coord = loc.coordinate
                                    mapView?.setRegion(
                                        MKCoordinateRegionMakeWithDistance(coord, 1000.0, 1000.0),
                                        animated = true
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
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
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedLocation = place
                                                            val coord = CLLocationCoordinate2DMake(place.latitude, place.longitude)
                                                            mapView?.setRegion(
                                                                MKCoordinateRegionMakeWithDistance(coord, 500.0, 500.0),
                                                                animated = true
                                                            )
                                                            mapView?.removeAnnotations(mapView!!.annotations)
                                                            val annotation = MKPointAnnotation()
                                                            annotation.setCoordinate(coord)
                                                            annotation.setTitle(place.name)
                                                            mapView?.addAnnotation(annotation)
                                                        }
                                                        .background(
                                                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                            else MaterialTheme.colorScheme.surface
                                                        )
                                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(RoundedCornerShape(10.dp))
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                                else MaterialTheme.colorScheme.surfaceVariant
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = selectedCategory!!.icon,
                                                            contentDescription = null,
                                                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = place.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                            else MaterialTheme.colorScheme.onSurface,
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
                                        }
                                    }
                                }

                                selectedLocation != null -> {
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
                                                text = selectedLocation!!.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (selectedLocation!!.address.isNotBlank()) {
                                                Text(
                                                    text = selectedLocation!!.address,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                        TextButton(onClick = {
                                            selectedLocation = null
                                            mapView?.removeAnnotations(mapView!!.annotations)
                                        }) {
                                            Text("Clear", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                        Button(
                                            onClick = { onLocationSelected(selectedLocation!!) },
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text("Select")
                                        }
                                    }
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
