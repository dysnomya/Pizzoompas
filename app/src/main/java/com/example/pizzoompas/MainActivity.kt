package com.example.pizzoompas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzoompas.map.MapScreen
import com.example.pizzoompas.ui.theme.PizzoompasTheme
import com.example.pizzoompas.utils.ManifestUtils
import com.example.pizzoompas.utils.findClosestPizzeria
import com.example.pizzoompas.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Retrieve the API key from the manifest file
        val apiKey = ManifestUtils.getApiKeyFromManifest(this)
        // Initialize the Places API with the retrieved API key
        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }
        val mapViewModel = MapViewModel()
        setContent {
            PizzoompasTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()

                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        AppDestinations.entries.forEach {
                            item(
                                icon = {
                                    Icon(
                                        it.icon,
                                        contentDescription = stringResource(it.contentDescription)
                                    )
                                },
                                label = { Text(stringResource(it.label)) },
                                selected = currentDestination?.destination?.route == it.route,
                                onClick = {
                                    navController.navigate(it.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    },

                    ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppDestinations.MAP.route
                    ) {
                        composable(AppDestinations.MAP.route) {
                            MapScreen(mapViewModel)
                        }

                        composable(AppDestinations.HOME.route) {
                            HomeScreen(mapViewModel)
                        }

                        composable(AppDestinations.COMPASS.route) {
                            CompassScreen(mapViewModel)
                        }
                    }
                }
            }
        }
    }
}

// Te dwa widoki sie potem przeniesie do osobnych plików czy coś

@Composable
fun HomeScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    mapViewModel.fetchUserLocation(context, fusedLocationClient)
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        Button(
            onClick = {
                findClosestPizzeria(userLocation!!.latitude, userLocation!!.longitude, context, mapViewModel)
            }
        ) {
            Text("Find nearest pizzeria!")
        }
    }
}

@Composable
fun CompassScreen(mapViewModel: MapViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        Text("Hiii")
    }
}

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int,
    val route: String
) {
    MAP(R.string.map, Icons.Outlined.Map, R.string.mapDescription, "map_route"),
    HOME(R.string.home, Icons.Outlined.Home, R.string.homeDescription, "home_route"),
    COMPASS(R.string.compass, Icons.Outlined.CompassCalibration, R.string.compassDescription, "compass_route")

}
