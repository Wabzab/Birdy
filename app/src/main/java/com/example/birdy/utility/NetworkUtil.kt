package com.example.birdy.utility

import android.net.Uri
import android.util.Log
import com.example.birdy.BuildConfig
import java.net.MalformedURLException
import java.net.URL

private const val TAXONOMY_URL = "https://api.ebird.org/v2/ref/taxonomy/ebird"
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

fun buildURLForTaxonomy(): URL? {
    val buildUri = Uri.parse(TAXONOMY_URL).buildUpon()
        .appendQueryParameter(
            PARAM_API_KEY,
            BuildConfig.EBIRD_API_KEY
        )
        .appendQueryParameter(
            "fmt",
            "json"
        )
        .build()
    var url: URL? = null
    try {
        url = URL(buildUri.toString())
    } catch (e: MalformedURLException) {
        e.printStackTrace()
    }
    Log.i(LOGGING_TAG, "buildURLForTaxonomy: $url")
    return url
    }
