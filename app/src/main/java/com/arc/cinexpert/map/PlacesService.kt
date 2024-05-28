package com.arc.cinexpert.map

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PlacesService {

    fun fetchNearbyCinemas(latitude: Double, longitude: Double, apiKey: String, callback: (List<Cinema>) -> Unit) {
        Thread {
            val urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&type=movie_theater&keyword=cine&radius=40000&key=$apiKey"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.connect()
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val data = connection.inputStream.bufferedReader().readText()
                    val jsonObject = JSONObject(data)
                    val results = parseCinemas(jsonObject)
                    callback(results)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection.disconnect()
            }
        }.start()
    }

    private fun parseCinemas(jsonObject: JSONObject): List<Cinema> {
        val resultsArray = jsonObject.getJSONArray("results")
        val cinemas = mutableListOf<Cinema>()
        for (i in 0 until resultsArray.length()) {
            val cinema = resultsArray.getJSONObject(i)
            val name = cinema.getString("name")
            val locationObj = cinema.getJSONObject("geometry").getJSONObject("location")
            val lat = locationObj.getDouble("lat")
            val lng = locationObj.getDouble("lng")
            cinemas.add(Cinema(name, LatLng(lat, lng)))
        }
        return cinemas
    }
}
