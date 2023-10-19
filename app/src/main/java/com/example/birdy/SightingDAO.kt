package com.example.birdy

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class SightingDAO(activity: Activity) {

    private val db = Firebase.firestore
    private var sharedPreferences: SharedPreferences
    private var activity: Activity

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        this.activity = activity
    }

    /*
        Saves a new sighting for the provided speciesCode for the logged in User.
        Creates a new collection for the species if this is the first sighting,
        otherwise adds a new entry to the existing species collection.
     */
    fun saveSighting(speciesCode: String): Boolean {
        val username = sharedPreferences.getString(activity.getString(R.string.saved_username_key), null) ?: return false
        val lat = sharedPreferences.getString(activity.getString(R.string.saved_lat_key), null) ?: return false
        val lng = sharedPreferences.getString(activity.getString(R.string.saved_lng_key), null) ?: return false
        val sight = hashMapOf(
            "lat" to lat.toDouble(),
            "lng" to lng.toDouble(),
            "date" to Calendar.getInstance().time
        )

        val sightingCollection = db.collection("sightings/$username/$speciesCode")
        val saveTask = sightingCollection.add(sight)
        val saveResult = kotlin.runCatching { Tasks.await(saveTask) }
        if (saveResult.isSuccess) {
            val userUpdateTask = db.collection("users").document(username).update("sightings", FieldValue.arrayUnion(speciesCode))
            kotlin.runCatching { userUpdateTask }
        }
        return saveResult.isSuccess
    }

    /*
        Returns an array of all sightings for the logged in User.
     */
    fun getSightings(): ArrayList<Sighting> {
        val username = sharedPreferences.getString(activity.getString(R.string.saved_username_key), null) ?: return ArrayList(emptyList())
        val userTask = db.collection("users").document(username).get()
        val user = kotlin.runCatching { Tasks.await(userTask) }.getOrNull() ?: return ArrayList(emptyList())
        val species = user.get("sightings") as List<*>
        if (species.isEmpty()) {
            return ArrayList(emptyList())
        }
        val sightings: ArrayList<Sighting> = ArrayList(emptyList())
        species.forEach { speciesCode ->
            val sightingsTask = db.collection("sightings/$username/$speciesCode").get()
            val sightingsResult = kotlin.runCatching { Tasks.await(sightingsTask) }.getOrNull() ?: return@forEach
            var sighting = Sighting("$speciesCode", ArrayList())
            sightingsResult.forEach {
                val lat = it.getDouble("lat") ?: return@forEach
                val lng = it.getDouble("lng") ?: return@forEach
                val date = it.getDate("date") ?: return@forEach
                val sight = Sight(lat, lng, date)
                sighting.sights.add(sight)
            }
            sightings.add(sighting)
        }
        sightings.forEach { sighting ->
            sighting.sights.forEach { sight ->
                Log.d("SIGHTINGS", "${sight.date}")
            }
        }
        return sightings
    }

}