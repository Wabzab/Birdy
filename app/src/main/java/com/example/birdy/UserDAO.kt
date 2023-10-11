package com.example.birdy

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
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

    // A coroutine function that waits for the success
    // Careful of using on the UI thread as it may cause the app to freeze...
    // https://www.reddit.com/r/Kotlin/comments/iceztd/how_to_return_value_from_from_a_listener/
    suspend fun registerUser(user: User): Boolean {
        val def = CompletableDeferred<Boolean>()
        val newUser = db.collection("users").document(user.username)
        newUser.get()
            .addOnSuccessListener {
                Log.d("USER", "Username already taken!")
                def.complete(false)
            }
            .addOnFailureListener {
                val data = hashMapOf(
                    "password" to user.password,
                    "email" to user.email,
                    "distance" to user.distance
                )
                newUser.set(data)
                    .addOnSuccessListener {
                        Log.d("USER", "${user.username} added!")
                        def.complete(true)
                    }
                    .addOnFailureListener {
                        Log.d("USER", "Error adding ${user.username}: $it")
                    }
            }
        return def.await()
    }

    // A coroutine function that waits for the success or failure of a user login attempt
    suspend fun loginUser(username: String, password: String): Boolean {
        val def = CompletableDeferred<Boolean>()
        // Check for name and password match
        val user = db.collection("users").document(username)
            .get()
            .addOnSuccessListener {
                if (it.get("password") == password) {
                    with (sharedPreferences.edit()) {
                        putInt("distance", it.get("distance") as Int)
                        putString("username", username)
                    }
                }
                Log.d("USER", "$username logged in!")
                def.complete(true)
            }
            .addOnFailureListener {
                Log.d("USER", "Username or password incorrect!")
                def.complete(false)
            }
        return def.await()
    }

}