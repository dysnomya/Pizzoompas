package com.example.pizzoompas.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.location.Location
import kotlin.math.roundToInt

const val UPDATE_FREQUENCY = 250
const val MINIMAL_DIFFERENCE_TO_LOAD_ADDRESS = 20 // meters

class CompassManager(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    var destination: Location? = null
        set(value) {
            field = value
            calculateAndNotifyDistance()
        }
    var currentLocation: Location? = null
        set(value) {
            field = value
            calculateAndNotifyDistance()
        }

    /**
     * rotation = device azimuth (0..360 degrees)
     * direction = angle to destination relative to device facing (null if unknown)
     */
    var onAzimuthChanged: ((rotation: Float, direction: Float?) -> Unit)? = null

    var onDistanceChanged: ((distanceMeters: Float?) -> Unit)? = null

    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravity, 0, 3)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
            }
        }

        val R = FloatArray(9)
        val I = FloatArray(9)

        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)

            // Azimuth (device rotation in degrees, 0..360)
            val azimuth = (Math.toDegrees(orientation[0].toDouble()) + 360) % 360

            // can round azimuth to nearest step to reduce jitter (surely)
//            val step = 10
//            val roundedAzimuth = (azimuth / step).roundToInt() * step

            // Calculate direction to destination relative to device facing
            val direction = if (currentLocation != null && destination != null) {
                val bearingToDest = currentLocation!!.bearingTo(destination!!)

                bearingToDest
            } else {
                null
            }

            onAzimuthChanged?.invoke(azimuth.toFloat(), direction)
        }
    }


    private fun calculateAndNotifyDistance() {
        val dist = if (currentLocation != null && destination != null) {
            val results = FloatArray(1)
            Location.distanceBetween(
                currentLocation!!.latitude,
                currentLocation!!.longitude,
                destination!!.latitude,
                destination!!.longitude,
                results
            )
            results[0]
        } else null

        onDistanceChanged?.invoke(dist)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}