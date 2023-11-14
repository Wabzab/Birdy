package com.example.birdy.observations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birdy.MainActivity
import com.example.birdy.R
import com.example.birdy.utility.Utils
import kotlin.concurrent.thread

class ViewObservations : AppCompatActivity() {

    lateinit var observationAdapter: ObservationAdapter
    lateinit var etSearchFilter: EditText
    lateinit var rvObservation: RecyclerView
    lateinit var btnReturn: ImageButton
    lateinit var btnAdd: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_observations)

        etSearchFilter = findViewById(R.id.etSearchFilter)
        rvObservation = findViewById(R.id.rvObservations)
        btnReturn = findViewById(R.id.ibtnViewReturn)
        btnAdd = findViewById(R.id.ibtnViewAdd)

        btnAdd.setOnClickListener {
            Utils.createObservationDialogue(this, ::saveSpecies)
        }

        btnReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

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
        observationAdapter = ObservationAdapter(sightings.toMutableList())
        rvObservation.adapter = observationAdapter
    }

    fun saveSpecies(species: Species) {
        val sightingDao = SightingDAO(this)
        if (species.equals(null)) {
            Toast.makeText(this, "Select a species!", Toast.LENGTH_SHORT).show()
            return
        }
        thread {
            Looper.prepare()
            val result = sightingDao.saveSighting(species)
            if (result) {
                Toast.makeText(this, "Observation saved successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ViewObservations::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to save details!", Toast.LENGTH_LONG).show()
            }
            Looper.loop()
        }
    }
}