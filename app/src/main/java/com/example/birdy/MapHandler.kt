package com.example.birdy

import android.app.Activity
import android.util.Log
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlin.concurrent.thread

object MapHandler {

    lateinit var map: GoogleMap

    fun loadHotspots(activity: Activity) {
        thread {
            Log.d("HOTSPOT", "Hotspot thread started.")
            val prefs = getDefaultSharedPreferences(activity)
            val lat = prefs.getString(activity.getString(R.string.saved_lat_key), "0")
            val lng = prefs.getString(activity.getString(R.string.saved_lng_key), "0")
            val dist = prefs.getInt(activity.getString(R.string.saved_dist_key), 0)
            Log.d("HOTSPOT", "$lat | $lng | $dist")
            val hotspotJson = try {
                if (lat != null && lng != null) {
                    buildURLForHotspot(lat, lng, dist)?.readText()
                } else {
                    Log.d("HOTSPOT", "Hotspot parameters invalid!")
                    return@thread
                }
            } catch (e: java.lang.Exception) {
                Log.d("HOTSPOT", "$e")
                return@thread
            }
            activity.runOnUiThread { readHotspotJson(hotspotJson) }
        }
    }

    private fun readHotspotJson(hotspotJson: String?) {
        Log.d("HOTSPOT", "Reading hotspot json.")
        val gson = Gson()
        val hotspotData: Array<Hotspot> = gson.fromJson(hotspotJson, Array<Hotspot>::class.java)
        createHotspotMarkers(hotspotData)
    }

    private fun createHotspotMarkers(hotspots: Array<Hotspot>) {
        for (hotspot in hotspots) {
            val lat = hotspot.lat
            val lng = hotspot.lng
            if (lat != null && lng != null) {
                val pos = LatLng(lat, lng)
                map.addMarker(MarkerOptions()
                    .position(pos)
                    .title(hotspot.locName)
                )
            }

        }
    }
}