package com.example.pizzoompas.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pizzoompas.R
import kotlinx.coroutines.delay

@Composable
fun RotatingPizza() {
    var rotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            rotation = rotation + 10f
            delay(500)
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(500, easing = LinearEasing)
    )

    Image(
        painter = painterResource(id = R.drawable.pizza),
        contentDescription = "pizza",
        modifier = Modifier
            .size(250.dp)
            .rotate(animatedRotation)
    )
}