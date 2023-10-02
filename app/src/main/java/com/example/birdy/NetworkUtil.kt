package com.example.birdy

import android.net.Uri
import android.util.Log
import java.net.MalformedURLException
import java.net.URL

private const val LAT = -34.03839620115185
private const val LNG = 18.349540783239803
private const val EBIRD_URL = "https://api.ebird.org/v2/data/obs/geo/recent?lat=${LAT}&lng=${LNG}"
private const val PARAM_HOTSPOT = "hotspot"
private const val HOTSPOT_VALUE = "true"
private const val PARAM_DIST = "dist"
private const val DIST_VALUE = "10"
private const val PARAM_API_KEY = "key"
private const val LOGGING_TAG = "NETWORK UTIL"

fun buildURLForEBird(): URL? {
    val buildUri: Uri = Uri.parse(EBIRD_URL).buildUpon()
        .appendQueryParameter(
            PARAM_API_KEY,
            BuildConfig.EBIRD_API_KEY
        ) // passing in api key
        .appendQueryParameter(
            PARAM_HOTSPOT,
            HOTSPOT_VALUE
        ) // passing in hotspot check
        .appendQueryParameter(
            PARAM_DIST,
            DIST_VALUE
        ) // passing in distance
        .build()
    var url: URL? = null
    try {
        url = URL(buildUri.toString())
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    Log.i(LOGGING_TAG, "buildURLForEBird: $url")
    return url
}