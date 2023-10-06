package com.example.birdy

import android.app.Activity
import android.util.Log
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.gson.Gson
import kotlin.concurrent.thread

/*
The interface for the application. The app needs to be able to display bird hotspots
that are within a certain radius around the user. It needs to display these hotspots
on a map and allow the user to select/navigate to them easily. The map needs to show
the route in the visual format.
 */

interface IBirdy {
    fun getHotspots(activity: Activity): List<Hotspot>

    fun getObservations()

    fun saveObservation()
}

object Birdy : IBirdy {
    override fun getHotspots(activity: Activity): List<Hotspot> {
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
            activity.runOnUiThread { consumeHotspotJson(hotspotJson) }
        }
    }

    private fun consumeHotspotJson(hotspotJson: String?) {
        Log.d("HOTSPOT", "Reading hotspot json.")
        if (hotspotJson == null) {
            return
        }
        val gson = Gson()
        val hotspotData: Array<Hotspot> = gson.fromJson(hotspotJson, Array<Hotspot>::class.java)
        Log.d("HOTSPOT", "${hotspotData.size}")
        for(hotspot in hotspotData) {
            Log.d("HOTSPOTS", "$hotspot")
        }
    }

    override fun getObservations() {
        TODO("Not yet implemented")
    }

    override fun saveObservation() {
        TODO("Not yet implemented")
    }

}