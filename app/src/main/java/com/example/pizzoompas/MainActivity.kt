package com.example.pizzoompas

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzoompas.model.PizzeriaDatabase
import com.example.pizzoompas.screens.MapScreen
import com.example.pizzoompas.ui.theme.PizzoompasTheme
import com.example.pizzoompas.utils.ManifestUtils
import com.example.pizzoompas.viewmodel.MapViewModel
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.delay
import com.example.pizzoompas.screens.SplashScreen
import com.example.pizzoompas.screens.CompassScreen
import com.example.pizzoompas.screens.HomeScreen
import com.example.pizzoompas.viewmodel.PizzeriaViewModel
import com.google.android.gms.location.LocationServices
import timber.log.Timber
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner


class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

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

        setContent {
            PizzoompasTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    val context = LocalContext.current
                    val activity = context as Activity

                    var showPermissionDialog by remember { mutableStateOf(false) }
                    var showInternetDialog by remember { mutableStateOf(false) }
                    var showLocationDialog by remember { mutableStateOf(false) }
                    var permissionsGranted by remember { mutableStateOf(false) }
                    var isInitialized by remember { mutableStateOf(false) }
                    var wasPermissionRequestedFromLauncher by remember { mutableStateOf(false) }

                    LaunchedEffect(permissionsGranted) {
                        if (permissionsGranted) {
                            showPermissionDialog = false
                        }
                    }


                    // Launcher for requesting permissions
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { result ->
                        permissionsGranted = result.all { it.value }

                        if (!permissionsGranted && wasPermissionRequestedFromLauncher) {
                            showPermissionDialog = true
                        }

                        wasPermissionRequestedFromLauncher = false
                    }



                    // First-run effect
                    LaunchedEffect(Unit) {
                        if (!hasLocationPermissions(context)) {
                            wasPermissionRequestedFromLauncher = true
                            permissionLauncher.launch(requiredPermissions)
                        } else {
                            permissionsGranted = true
                            if (!isConnectedToInternet(context)) {
                                showInternetDialog = true
                            } else if (!isLocationEnabled(context)) {
                                showLocationDialog = true
                            }
                        }
                        isInitialized = true
                    }

                    LaunchedEffect(permissionsGranted) {
                        if (permissionsGranted) {
                            if (isConnectedToInternet(context)) {
                                showInternetDialog = false
                            }
                            if (isLocationEnabled(context)) {
                                showLocationDialog = false
                            }
                        }
                    }




                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_RESUME) {
                                val hasPermission = hasLocationPermissions(context)
                                permissionsGranted = hasPermission

                                if (!hasPermission && !wasPermissionRequestedFromLauncher) {
                                    showPermissionDialog = true
                                } else if (hasPermission) {
                                    if (!isConnectedToInternet(context)) {
                                        showInternetDialog = true
                                    } else {
                                        showInternetDialog = false
                                    }

                                    if (!isLocationEnabled(context)) {
                                        showLocationDialog = true
                                    } else {
                                        showLocationDialog = false
                                    }
                                }

                                isInitialized = true
                            }
                        }


                        val lifecycle = lifecycleOwner.lifecycle
                        lifecycle.addObserver(observer)

                        onDispose {
                            lifecycle.removeObserver(observer)
                        }
                    }



                    // Main app content
                    if (permissionsGranted) {
                        MainScreen()
                    }

                    // Permission dialog
                    if (showPermissionDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = { Text("Wymagane uprawnienia") },
                            text = { Text("Należy dać uprawnienia dostępu do lokalizacji urządzenia w celu korzystania z aplikacji.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    openAppSettings(context)
                                    showPermissionDialog = false
                                }) {
                                    Text("Otwórz ustawienia aplikacji")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    activity.finish()
                                }) {
                                    Text("Wyjdź")
                                }
                            }
                        )
                    }

                    // Internet dialog
                    if (showInternetDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = { Text("Brak połączenia internetowego") },
                            text = { Text("Do korzystania z aplikacji jest wymagane połączenie internetowe.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                    showInternetDialog = false
                                }) {
                                    Text("Otwórz ustawienia WiFi")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    activity.finish()
                                }) {
                                    Text("Wyjdź")
                                }
                            }
                        )
                    }

                    if (showLocationDialog) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = { Text("Lokalizacja wyłączona") },
                            text = { Text("Wymagany jest dostęp do aktualnej lokalizacji urządzenia.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                    showLocationDialog = false
                                }) {
                                    Text("Otwórz ustawienia")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    activity.finish()
                                }) {
                                    Text("Wyjdź")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()

    val mapViewModel : MapViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel() as T
            }
        }
    )
    val pizzeriaViewModel : PizzeriaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PizzeriaViewModel(PizzeriaDatabase.getDatabase(context).pizzeriaDao()) as T
            }
        }
    )

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
                MapScreen(mapViewModel, pizzeriaViewModel)
            }

            composable(AppDestinations.HOME.route) {
                HomeScreen(mapViewModel, pizzeriaViewModel)
            }

            composable(AppDestinations.COMPASS.route) {
                CompassScreen(mapViewModel)
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
