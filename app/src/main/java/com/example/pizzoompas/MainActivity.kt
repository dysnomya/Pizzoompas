package com.example.pizzoompas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzoompas.map.Map
import com.example.pizzoompas.ui.theme.PizzoompasTheme
import com.example.pizzoompas.viewmodel.MapViewModel


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PizzoompasTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()
                val mapViewModel = MapViewModel()

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
                            Map(mapViewModel)
                        }

                        composable(AppDestinations.EMPTY.route) {
                            EmptyShit()
                        }

                        composable(AppDestinations.PIZZA.route) {
                            EmptyShit()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyShit() {
    Text("Hiiii")
}

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int,
    val route: String
) {
    MAP(R.string.map, Icons.Outlined.Map, R.string.mapDescription, "map_route"),
    EMPTY(R.string.aaa, Icons.Outlined.Fastfood, R.string.aaaDescription, "aaa_route"),
    PIZZA(R.string.pizza, Icons.Outlined.Accessibility, R.string.pizzaDescription, "pizza_route")
}
