package com.example.pizzoompas.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.pizzoompas.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pizzoompas.viewmodel.PizzeriaViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import timber.log.Timber

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    pizzeriaViewModel: PizzeriaViewModel
) {
    // Initialize the camera position state, which controls the camera's position on the map
    val cameraPositionState = rememberCameraPositionState()
    // Obtain the current context
    val context = LocalContext.current
    // Observe the user's location from the ViewModel
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Observe the selected location from the ViewModel
    val currentPizzeria by pizzeriaViewModel.currentPizzeria
    val currentPizzeriaLocation by mapViewModel.closestPizzeriaLocation


    // Layout that includes the search bar and the map, arranged in a vertical column
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(18.dp)) // Add a spacer with a height of 18dp to push the search bar down

        // Add the search bar component
//        SearchBar(
//            onPlaceSelected = { place ->
//                // When a place is selected from the search bar, update the selected location
//                mapViewModel.selectLocation(place, context)
//            }
//        )
//        Button(
//            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
//            onClick = {
//                findClosestPizzeria(userLocation!!.latitude, userLocation!!.longitude, context, mapViewModel)
//            }
//        ) {
//            Text("Find nearest pizzeria!")
//        }

        // Display the Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // If the user's location is available, place a marker on the map
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it), // Place the marker at the user's location
                    title = "Jesteś tutaj", // Set the title for the marker
                    snippet = "To jest twoja przybliżona aktualna lokalizacja" // Set the snippet for the marker
                )
                // Move the camera to the user's location with a zoom level of 10f
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }

            currentPizzeriaLocation?.let {
                Marker(
                    state = MarkerState(position = it), // Place the marker at the selected location
                    title = currentPizzeria!!.name, // Set the title for the marker
                    snippet = currentPizzeria!!.address // Set the snippet for the marker
                )
                // Move the camera to the selected location with a zoom level of 15f
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }
    }
}