package com.example.pizzoompas.screens

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzoompas.viewmodel.MapViewModel
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.pizzoompas.R
import com.example.pizzoompas.utils.CompassManager
import com.example.pizzoompas.utils.UPDATE_FREQUENCY
import com.example.pizzoompas.utils.toLocation

val COMPASS_PADDING = 16.dp


@Composable
fun CompassScreen(mapViewModel: MapViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var rotation by remember { mutableFloatStateOf(0f) }
        var direction by remember { mutableStateOf<Float?>(null) }
        var distanceMeters by remember { mutableStateOf<Float?>(null) }

        val destinationLocation by mapViewModel.closestPizzeriaLocation
        val currentLocation by mapViewModel.userLocation

        CompassHeading(
            destination = destinationLocation?.toLocation(),
            currentLocation = currentLocation?.toLocation(),
            onAzimuthUpdate = { newRotation, newDirection ->
                rotation = newRotation
                direction = newDirection
            },
            onDistanceUpdate = { newDistance ->
                distanceMeters = newDistance
            }
        )

        Compass(direction = direction?.toInt(), rotation = rotation.toInt(), distance = distanceMeters)

//        Spacer(modifier = Modifier.height(200.dp))
//        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun CompassHeading(
        destination: Location?, currentLocation: Location?,
        onAzimuthUpdate: (rotation: Float, direction: Float?) -> Unit,
        onDistanceUpdate: (Float?) -> Unit
    ) {
    val context = LocalContext.current
    val compass = remember { CompassManager(context) }

    LaunchedEffect(destination, currentLocation) {
        compass.destination = destination
        compass.currentLocation = currentLocation
    }

    DisposableEffect(Unit) {
        compass.onAzimuthChanged = onAzimuthUpdate
        compass.onDistanceChanged = onDistanceUpdate
        compass.startListening()

        onDispose {
            compass.stopListening()
        }
    }
}

@Composable
fun Compass(
    direction: Int?,
    rotation: Int,
    distance: Float?,
) {
    val (lastRotation, setLastRotation) = remember { mutableStateOf(0) }
    var newRotation = lastRotation
    val modLast = if (lastRotation > 0) lastRotation % 360 else 360 - (-lastRotation % 360)

    if (modLast != rotation)
    {
        // new rotation comes in
        val backward = if (rotation > modLast) modLast + 360 - rotation else modLast - rotation
        val forward = if (rotation > modLast) rotation - modLast else 360 - modLast + rotation

        newRotation = if (backward < forward)
        {
            // backward rotation is shorter
            lastRotation - backward
        }
        else
        {
            // forward rotation is shorter (or they are equals)
            lastRotation + forward
        }

        setLastRotation(newRotation)
    }

    val angle: Float by animateFloatAsState(
        targetValue = -newRotation.toFloat(),
        animationSpec = tween(
            durationMillis = UPDATE_FREQUENCY,
            easing = LinearEasing
        )
    )

    Column()
    {
        Box(
            modifier = Modifier
                .padding(COMPASS_PADDING),
            contentAlignment = Alignment.Center
        ) {
            if (direction != null) {
                CompassDirectionPointer(
                    angle = angle + direction.toFloat(),
                    pointerIcon = R.drawable.slice,
                    contentDsc = R.string.destination_direction
                )
            }

            Rose(angle = angle, distance = distance)
        }
    }


}

@Composable
fun Rose(
    angle: Float,
    distance: Float?,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .fillMaxSize()
            .rotate(angle),
        painter = painterResource(id = R.drawable.ic_rose),
        contentDescription = stringResource(id = R.string.compass),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(
            color = MaterialTheme.colorScheme.onBackground
        )
    )

    Text(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        text = stringResource(id = R.string.meter_format, (distance ?: 0f).toInt()),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun CompassDirectionPointer(
    @DrawableRes pointerIcon: Int,
    @StringRes contentDsc: Int,
    modifier: Modifier = Modifier,
    angle: Float = 0f,
)
{
    Image(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .padding(COMPASS_PADDING)
            .rotate(degrees = angle)
            .offset(y = 100.dp),
        painter = painterResource(id = pointerIcon),
        contentDescription = stringResource(id = contentDsc),
        contentScale = ContentScale.Fit,
    )
}
