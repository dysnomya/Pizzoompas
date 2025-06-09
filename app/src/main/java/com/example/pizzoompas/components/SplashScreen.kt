package com.example.pizzoompas.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pizzoompas.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RotatingIcon()
    }
}

@Composable
fun RotatingIcon() {
    var rotation by remember { mutableFloatStateOf(0f) }
    val scale = remember { Animatable(0f) }

    // Scaling
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        scale.animateTo(
            targetValue = 0.0f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }

    // Rotating
    LaunchedEffect(Unit) {
        while (true) {
            rotation = rotation + 10f
            delay(10)
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(200, easing = LinearEasing)
    )

    Image(
        painter = painterResource(id = R.drawable.icon),
        contentDescription = "Logo",
        modifier = Modifier
            .size(100.dp)
            .rotate(animatedRotation)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
    )

}