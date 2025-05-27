package com.example.pizzoompas

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzoompas.map.Map
import com.example.pizzoompas.ui.theme.PizzoompasTheme

class MainActivity : ComponentActivity() {
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )

        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }

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
                            Map()
                        }

                        composable(AppDestinations.EMPTY.route) {
                            Empty(applicationContext)
                        }
                    }
                }
            }
        }
    }



}

@Composable
fun Empty(
    applicationContext: Context
) {
    Column {
        Text("Hiiii")
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    applicationContext.startService(this)
                }
            }
        ) {
            Text("Start tracking")
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                    applicationContext.startService(this)
                }
            }
        ) {
            Text("Stop tracking")
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
    EMPTY(R.string.aaa, Icons.Outlined.Fastfood, R.string.aaaDescription, "aaa_route"),
}
