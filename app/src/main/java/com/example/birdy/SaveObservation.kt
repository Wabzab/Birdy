package com.example.birdy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Filter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlin.concurrent.thread

class SaveObservation : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var speciesAdapter: ArrayAdapter<Species>
    lateinit var spnSpecies: Spinner
    lateinit var btnSave: Button
    lateinit var etFilter: EditText
    lateinit var tvComName: TextView
    lateinit var tvSciName: TextView
    lateinit var tvFamComName: TextView
    lateinit var tvFamSciName: TextView
    lateinit var tvOrder: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_observation)

        spnSpecies = findViewById(R.id.spnSpecies)
        btnSave = findViewById(R.id.btnSave)
        etFilter = findViewById(R.id.etFilter)
        tvComName = findViewById(R.id.tvComName)
        tvSciName = findViewById(R.id.tvSciName)
        tvFamComName = findViewById(R.id.tvFamComName)
        tvFamSciName = findViewById(R.id.tvFamSciName)
        tvOrder = findViewById(R.id.tvOrder)

        btnSave.setOnClickListener {
            val intent = Intent(this, ViewObservations::class.java)
            startActivity(intent)
        }

        etFilter.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                speciesAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

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
        speciesAdapter = ArrayAdapter(this, R.layout.species_item, R.id.tvCommonName, speciesData)
        speciesAdapter.setDropDownViewResource(R.layout.species_item)
        spnSpecies.adapter = speciesAdapter
        spnSpecies.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (parent != null) {
            val item: Species = parent.getItemAtPosition(pos) as Species
            tvComName.text = item.common_name
            tvSciName.text = item.scientific_name
            tvFamComName.text = item.family_com_name
            tvFamSciName.text = item.family_sci_name
            tvOrder.text = item.order
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}