package com.example.birdy

import com.google.gson.annotations.SerializedName

data class User(
    var username: String,
    var password: String,
    var email: String,
    var distance: Int
)
