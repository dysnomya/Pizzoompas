package com.example.pizzoompas.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzoompas.model.Pizzeria
import com.example.pizzoompas.model.PizzeriaDao
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class PizzeriaViewModel(private val pizzeriaDao: PizzeriaDao) : ViewModel() {
    private val _currentPizzeria = mutableStateOf<Pizzeria?>(null)
    val currentPizzeria: State<Pizzeria?> = _currentPizzeria

    fun insert(pizzeria: Pizzeria) {
        viewModelScope.launch {
            pizzeriaDao.insert(pizzeria)
        }
    }
    fun update(pizzeria: Pizzeria) {
        viewModelScope.launch {
            pizzeriaDao.update(pizzeria)
        }
    }
    fun delete(pizzeria: Pizzeria) {
        viewModelScope.launch {
            pizzeriaDao.delete(pizzeria)
        }
    }
    fun getPizzeriaByLatLng(lat: Double, lng: Double) {
        viewModelScope.launch {
            _currentPizzeria.value = pizzeriaDao.getPizzeriaByLatLng(lat, lng)
        }
    }
    fun deleteAll() {
        viewModelScope.launch {
            pizzeriaDao.deleteAll()
        }
    }
}