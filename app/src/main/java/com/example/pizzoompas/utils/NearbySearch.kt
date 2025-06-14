package com.example.pizzoompas.utils

import android.content.Context
import com.example.pizzoompas.model.Pizzeria
import com.example.pizzoompas.viewmodel.MapViewModel
import com.example.pizzoompas.viewmodel.PizzeriaViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Callback
import okhttp3.Call
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

fun findClosestPizzeria(
    lat: Double,
    lng: Double,
    context: Context,
    mapViewModel: MapViewModel,
    pizzeriaViewModel: PizzeriaViewModel
) {
    val apiKey = ManifestUtils.getApiKeyFromManifest(context)
    val type = "restaurant"
    val keyword = "pizzeria"

    val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
            "?location=$lat,$lng" +
            "&rankby=distance" +
            "&type=$type" +
            "&keyword=$keyword" +
            "&key=$apiKey"

    val request = Request.Builder().url(url).build()
    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Timber.tag("PlacesAPI").e("Request failed: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use { // Auto-closes the response
                if (!response.isSuccessful) {
                    Timber.tag("PlacesAPI").e("Unexpected code $response")
                    return
                }

                val body = response.body?.string()
                if (body != null) {
                    val json = JSONObject(body)
                    val results = json.getJSONArray("results")

                    if (results.length() > 0) {
                        val firstPlace = results.getJSONObject(0)
                        val name = firstPlace.getString("name")
                        val address = firstPlace.getString("vicinity")
                        val rating = firstPlace.optDouble("rating", -1.0)
                        val userRatingsTotal = firstPlace.optInt("user_ratings_total", 0)
                        val photos = firstPlace.optJSONArray("photos")
                        val photoReference = photos?.optJSONObject(0)?.optString("photo_reference")

                        val maxWidth = 400 // or maxheight
                        val photoUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                                "?maxwidth=$maxWidth" +
                                "&photoreference=$photoReference" +
                                "&key=$apiKey"

                        val location = firstPlace.getJSONObject("geometry").getJSONObject("location")
                        val lat = location.getDouble("lat")
                        val lng = location.getDouble("lng")

                        val pizzeria = Pizzeria(
                            name = name,
                            address = address,
                            rating = rating,
                            userRatingsTotal = userRatingsTotal,
                            iconURL = photoUrl,
                            latitude = lat,
                            longitude = lng
                        )
                        //pizzeriaViewModel.deleteAll()
                        pizzeriaViewModel.insert(pizzeria)
                        
                        mapViewModel.setClosestPizzeriaLatLng(lat, lng)
                        mapViewModel.startNavigation()

                        Timber.tag("PlacesAPI")
                            .d("Closest Pizzeria: $name at $address ($lat, $lng)")
                    } else {
                        Timber.tag("PlacesAPI").d("No pizzerias found.")
                    }
                }
            }
        }


    })
}