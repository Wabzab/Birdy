package com.example.birdy

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class Settings : AppCompatActivity() {

    lateinit var etDistance: EditText
    lateinit var constraintLayout: ConstraintLayout

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        etDistance = findViewById(R.id.etDistance)
        constraintLayout = findViewById(R.id.clMain)

        val sharedPrefs = getDefaultSharedPreferences(this)
        val dist = sharedPrefs.getInt(this.getString(R.string.saved_dist_key), 0)
        etDistance.setText("$dist")

        val switchDarkMode: Switch = findViewById(R.id.switchDarkMode)
        switchDarkMode.setOnClickListener {
            constraintLayout.setBackgroundColor(Color.DKGRAY)
        }
    }
}