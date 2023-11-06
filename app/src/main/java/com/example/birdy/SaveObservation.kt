package com.example.birdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlin.concurrent.thread

class SaveObservation : AppCompatActivity() {

    lateinit var rvSpecies: RecyclerView
    lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_observation)

        rvSpecies = findViewById(R.id.rvSpecies)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val intent = Intent(this, ViewObservations::class.java)
            startActivity(intent)
        }

        thread {
            val taxonomyJson = try {
                buildURLForTaxonomy()?.readText()
            } catch (e: java.lang.Exception) {
                Log.d("TAXONOMY", "$e")
                return@thread
            }
            val gson = Gson()
            val taxonomyData: Array<Species> = gson.fromJson(taxonomyJson, Array<Species>::class.java)
            runOnUiThread { populateSpeciesRecycler(taxonomyData) }
        }
    }

    private fun populateSpeciesRecycler(speciesData: Array<Species>) {
        val speciesAdapter = SpeciesAdapter(speciesData)
        rvSpecies.adapter = speciesAdapter
    }
}