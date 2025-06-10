package com.example.pizzoompas.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PizzeriaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pizzeria: Pizzeria)
    @Update
    suspend fun update(pizzeria: Pizzeria)
    @Delete
    suspend fun delete(pizzeria: Pizzeria)
    @Query("SELECT * FROM pizzerias WHERE latitude = :lat AND longitude = :lng")
    suspend fun getPizzeriaByLatLng(lat: Double, lng: Double): Pizzeria
    @Query("DELETE FROM pizzerias")
    suspend fun deleteAll()
}