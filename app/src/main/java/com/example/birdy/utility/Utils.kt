package com.example.birdy.utility

import android.util.Base64
import android.util.Log
import java.security.MessageDigest

object Utils {

    // Takes a plaintext password and returns a hashed password using SHA-256
    fun hashPass(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }

    // Checks if the plaintext password matches the hashed password
    fun isPassValid(password: String, hash: String): Boolean {
        val digest = hashPass(password)
        Log.d("SECURITY", "$digest | $hash")
        return MessageDigest.isEqual(digest.toByteArray(Charsets.UTF_8), hash.toByteArray(Charsets.UTF_8))
    }

}