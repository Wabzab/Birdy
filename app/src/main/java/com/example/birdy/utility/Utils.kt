package com.example.birdy.utility

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.birdy.R
import com.example.birdy.observations.Species
import com.google.gson.Gson
import java.security.MessageDigest
import kotlin.concurrent.thread


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

    fun createObservationDialogue(activity: Activity, callback: (input: Species) -> Unit) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_species_dialog)

        val width = (activity.resources.displayMetrics.widthPixels * 0.90).toInt()

        val spnSpecies = dialog.findViewById<Spinner>(R.id.spnSpecies)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val etFilter = dialog.findViewById<EditText>(R.id.etFilter)
        val tvComName = dialog.findViewById<TextView>(R.id.tvComName)
        val tvSciName = dialog.findViewById<TextView>(R.id.tvSciName)
        val tvFamComName = dialog.findViewById<TextView>(R.id.tvFamComName)
        val tvFamSciName = dialog.findViewById<TextView>(R.id.tvFamSciName)
        val tvOrder = dialog.findViewById<TextView>(R.id.tvOrder)
        val tvEbirdLink = dialog.findViewById<TextView>(R.id.tvEbirdLink)

        tvEbirdLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ebird.org/home"))
            startActivity(activity, browserIntent, null)
        }

        lateinit var adapter: ArrayAdapter<Species>
        var selectedSpecies: Species? = null

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            if (selectedSpecies != null) {
                callback(selectedSpecies!!)
                dialog.dismiss()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        }

        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (parent != null) {
                    val item: Species = parent.getItemAtPosition(pos) as Species
                    selectedSpecies = item
                    tvComName.text = item.common_name
                    tvSciName.text = item.scientific_name
                    tvFamComName.text = item.family_com_name
                    tvFamSciName.text = item.family_sci_name
                    tvOrder.text = item.order
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                return
            }

        }

        thread {
            val taxonomyJson = try {
                buildURLForTaxonomy()?.readText()
            } catch (e: Exception) {
                Log.d("TAXONOMY", "$e")
                return@thread
            }
            val gson = Gson()
            val taxonomyData: Array<Species> = gson.fromJson(taxonomyJson, Array<Species>::class.java)
            activity.runOnUiThread {
                adapter = ArrayAdapter(activity, R.layout.species_item, R.id.tvCommonName, taxonomyData)
                adapter.setDropDownViewResource(R.layout.species_item)
                spnSpecies.adapter = adapter
                spnSpecies.onItemSelectedListener = itemSelectedListener
                etFilter.addTextChangedListener(textWatcher)
            }
        }

        dialog.show()
        dialog.window?.setLayout(width, -2)
    }

}