package com.example.birdy.observations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birdy.R
import kotlin.concurrent.thread

class ViewObservations : AppCompatActivity() {

    lateinit var observationAdapter: ObservationAdapter
    lateinit var etSearchFilter: EditText
    lateinit var btnCancel: ImageButton
    lateinit var rvObservation: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_observations)

        etSearchFilter = findViewById(R.id.etSearchFilter)
        btnCancel = findViewById(R.id.ibtnCancel)
        rvObservation = findViewById(R.id.rvObservations)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sightingDAO = SightingDAO(this)
        thread {
            val sightings = sightingDAO.getSightings(sharedPreferences.getString(getString(R.string.saved_username_key), "") ?: "")
            runOnUiThread { populateSightings(sightings.toList()) }
        }

        etSearchFilter.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                observationAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })
    }

    fun populateSightings(sightings: List<Sighting>) {
        observationAdapter = ObservationAdapter(sightings.toList())
        rvObservation.adapter = observationAdapter
    }
}