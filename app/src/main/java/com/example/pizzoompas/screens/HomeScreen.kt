package com.example.pizzoompas.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.pizzoompas.utils.findClosestPizzeria
import com.example.pizzoompas.viewmodel.MapViewModel
import com.example.pizzoompas.viewmodel.PizzeriaViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun HomeScreen(
    mapViewModel: MapViewModel,
    pizzeriaViewModel: PizzeriaViewModel
) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation
    val navigating by mapViewModel.navigating
    val closestPizzeriaLocation by mapViewModel.closestPizzeriaLocation
    val currentPizzeria by pizzeriaViewModel.currentPizzeria
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    mapViewModel.fetchUserLocation(context, fusedLocationClient)

    mapViewModel.startLocationUpdates(context, fusedLocationClient)

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
            val buttonModifier = Modifier.height(50.dp).width(300.dp)
            if (!navigating) {
                Button(
                    onClick = {
                        findClosestPizzeria(userLocation?.latitude ?: 0.0,
                            userLocation?.longitude ?: 0.0, context, mapViewModel, pizzeriaViewModel)
                    },
                    modifier = buttonModifier
                ) {
                    Text("Znajdź najbliższą pizzerię", color = MaterialTheme.colorScheme.onPrimary)
                }
            } else {
                Button(
                    onClick = {
                        mapViewModel.cancelNavigation()
                    },
                    modifier = buttonModifier
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!navigating) {
                Text("Tutaj pojawią się informacje o znalezionej pizzerii", color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
            } else {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(300.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    AsyncImage(
                        model = currentPizzeria?.iconURL,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(currentPizzeria?.name ?: "Nazwa pizzerii", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(currentPizzeria?.address ?: "Adres pizzerii")
                Text(String.format("Opinie: " + currentPizzeria?.rating))
                Text(String.format("Liczba opinii: " + currentPizzeria?.userRatingsTotal))
            }

        }

    }
}