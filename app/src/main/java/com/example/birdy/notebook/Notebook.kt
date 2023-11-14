package com.example.birdy.notebook

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.example.birdy.R
import com.example.birdy.observations.Species
import com.example.birdy.utility.buildURLForTaxonomy
import com.google.gson.Gson
import kotlin.concurrent.thread

class Notebook : AppCompatActivity() {

    lateinit var rvNotebook: RecyclerView
    lateinit var btnNoteAdd: ImageButton
    lateinit var notesAdapter: NotebookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook)

        rvNotebook = findViewById(R.id.rvNotebook)
        btnNoteAdd = findViewById(R.id.ibtnNoteAdd)


        btnNoteAdd.setOnClickListener {
            showAddDialog()
        }

        updateNotes()
    }

    fun updateNotes() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val noteDao = NoteDao(this)
        thread {
            val notes = noteDao.fetchNotes(sharedPreferences.getString(getString(R.string.saved_username_key), "") ?: "")
            runOnUiThread { populateNotes(notes.toMutableList()) }
        }
    }

    fun populateNotes(notes: MutableList<Note>) {
        notesAdapter = NotebookAdapter(notes, this)
        rvNotebook.adapter = notesAdapter
    }

    fun showAddDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_species_dialog)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()

        val spnSpecies = dialog.findViewById<Spinner>(R.id.spnSpecies)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)
        val etFilter = dialog.findViewById<EditText>(R.id.etFilter)
        val tvComName = dialog.findViewById<TextView>(R.id.tvComName)
        val tvSciName = dialog.findViewById<TextView>(R.id.tvSciName)
        val tvFamComName = dialog.findViewById<TextView>(R.id.tvFamComName)
        val tvFamSciName = dialog.findViewById<TextView>(R.id.tvFamSciName)
        val tvOrder = dialog.findViewById<TextView>(R.id.tvOrder)

        lateinit var adapter: ArrayAdapter<Species>
        var selectedSpecies: Species? = null

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            if (selectedSpecies != null) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val noteDao = NoteDao(this)
                thread {
                    val user = sharedPreferences.getString(getString(R.string.saved_username_key), "") ?: ""
                    val note = Note(selectedSpecies!!, false)
                    val result = noteDao.addNote(user, note)
                    runOnUiThread {
                        notesAdapter.add(note)
                    }
                }
                dialog.dismiss()
            }
        }

        val textWatcher = object : TextWatcher{
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
            } catch (e: java.lang.Exception) {
                Log.d("TAXONOMY", "$e")
                return@thread
            }
            val gson = Gson()
            val taxonomyData: Array<Species> = gson.fromJson(taxonomyJson, Array<Species>::class.java)
            runOnUiThread {
                adapter = ArrayAdapter(this, R.layout.species_item, R.id.tvCommonName, taxonomyData)
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