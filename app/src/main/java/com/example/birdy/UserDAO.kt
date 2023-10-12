package com.example.birdy

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred

/*
Handles any and all interactions with the `users` collection on the Firebase Firestore.
This involves user registration and user login.
 */

class UserDAO(activity: Activity) {

    private val db = Firebase.firestore
    var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    /*
        Registers a new User in Firestore if the name is not already taken.
        Returns a boolean value that represents the result (Success/Failure)
        Must be run on a separate thread with a Looper else it won't work!
    */
    fun registerUser(user: User): Boolean {
        val newUser = db.collection("users").document(user.username)
        val getTask = newUser.get()
        val getResult = kotlin.runCatching { Tasks.await(getTask) }
        val doc = getResult.getOrNull() ?: return false
        if (!doc.exists()) {
            val data = hashMapOf(
                "password" to Utils.hashPass(user.password),
                "email" to user.email,
                "distance" to user.distance
            )
            val setTask = newUser.set(data)
            val setResult = kotlin.runCatching { Tasks.await(setTask) }
            Log.d("USER", "User registered!")
            return setResult.isSuccess
        }
        Log.d("USER", "Username taken!")
        return false
    }

    /*
        Logs in a user using the provided username and password credentials.
        Returns a boolean value that represent the result (Success/Failure)
        Must be run on a separate thread with a Looper else it won't work!
     */
    fun loginUser(username: String, password: String): Boolean {
        val getTask = db.collection("users").document(username).get()
        val getResult = kotlin.runCatching { Tasks.await(getTask) }
        val doc = getResult.getOrNull() ?: return false
        if (doc.exists()) {
            if (Utils.isPassValid(password, doc.get("password") as String)) {
                with (sharedPreferences.edit()) {
                    val distance = doc.get("distance") as Long
                    putInt("distance", distance.toInt())
                    putString("username", username)
                }
                Log.d("USER", "User logged in!")
                return true
            }
            Log.d("USER", "Incorrect password!")
            return false
        }
        Log.d("USER", "No user found!")
        return false
    }
}