package com.example.pizzoompas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pizzoompas.utils.findClosestPizzeria
import com.example.pizzoompas.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun HomeScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation
    val navigating by mapViewModel.navigating
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    mapViewModel.fetchUserLocation(context, fusedLocationClient)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        //Spacer(modifier = Modifier.height(200.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!navigating) {
                Button(
                    onClick = {
                        findClosestPizzeria(userLocation!!.latitude, userLocation!!.longitude, context, mapViewModel)
                    }
                ) {
                    Text("Znajdź najbliższą pizzerię", color = MaterialTheme.colorScheme.onPrimary)
                }
            } else {
                Button(
                    onClick = {
                        mapViewModel.cancelNavigation()
                    }
                ) {
                    Text("Anuluj nawigację", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        HorizontalDivider()
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            if (!navigating) {
                Text("Tutaj pojawią się informacje o znalezionej pizzerii", color = MaterialTheme.colorScheme.onBackground)
            } else {
                Text("Nazwa pizzerii")
                Text("Dane pizzerii")
                Text("Bla bla bla")
            }

        }

    }
}