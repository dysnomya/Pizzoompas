package com.example.pizzoompas.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun LatLng.toLocation(): Location = Location("converted").apply {
    latitude = this@toLocation.latitude
    longitude = this@toLocation.longitude
}