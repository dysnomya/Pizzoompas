package com.example.pizzoompas.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pizzeria(
    val id: String? = null,
    val displayName: String? = null,
    val formatedAddress: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Parcelable