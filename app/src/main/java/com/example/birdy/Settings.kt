package com.example.birdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class Settings : AppCompatActivity() {

    lateinit var etDistance: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        etDistance = findViewById(R.id.etDistance)

        val sharedPrefs = getDefaultSharedPreferences(this)
        val dist = sharedPrefs.getInt(this.getString(R.string.saved_dist_key), 0)
        etDistance.setText("$dist")
    }
}