package com.example.pizzoompas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pizzerias")
data class Pizzeria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val rating: Double,
    val userRatingsTotal: Int,
    val iconURL: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)