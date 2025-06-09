package com.example.pizzoompas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzoompas.screens.MapScreen
import com.example.pizzoompas.ui.theme.PizzoompasTheme
import com.example.pizzoompas.utils.ManifestUtils
import com.example.pizzoompas.viewmodel.MapViewModel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.delay
import com.example.pizzoompas.screens.SplashScreen
import com.example.pizzoompas.screens.CompassScreen
import com.example.pizzoompas.screens.HomeScreen


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
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
                var showSplash by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
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
                            startDestination = AppDestinations.HOME.route
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
