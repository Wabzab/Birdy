package com.example.birdy

import android.net.Uri
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.net.MalformedURLException
import java.net.URL

private const val ROUTES_URL = "https://routes.googleapis.com/directions/v2:computeRoutes"
private const val HOTSPOT_URL = "https://api.ebird.org/v2/ref/hotspot/geo?"
private const val PARAM_API_KEY = "key"
private const val LOGGING_TAG = "NETWORK UTIL"

fun buildURLForHotspot(lat: String, lng: String, dist: Int): URL? {
    Log.d("HOTSPOT", "Building URL for hotspots.")
    val buildUri = Uri.parse(HOTSPOT_URL).buildUpon()
        .appendQueryParameter(
            PARAM_API_KEY,
            BuildConfig.EBIRD_API_KEY
        )
        .appendQueryParameter(
            "lat",
            lat
        )
        .appendQueryParameter(
            "lng",
            lng
        )
        .appendQueryParameter(
            "dist",
            dist.toString()
        )
        .appendQueryParameter(
            "fmt",
            "json"
        ).build()
    var url: URL? = null
    try {
        url = URL(buildUri.toString())
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    Log.i(LOGGING_TAG, "buildURLForHotspot: $url")
    return url
}

fun buildURLForRoute(origin: LatLng, dest: LatLng): URL {
    val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&sensor=false" +
            "&mode=driving" +
            "&key=${BuildConfig.MAPS_API_KEY}"
    return URL(url)
}
