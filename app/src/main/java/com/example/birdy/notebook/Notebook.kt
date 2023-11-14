package com.example.birdy.notebook

import android.app.Dialog
import android.content.Intent
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
import com.example.birdy.MainActivity
import com.example.birdy.R
import com.example.birdy.observations.Species
import com.example.birdy.observations.ViewObservations
import com.example.birdy.utility.Utils
import com.example.birdy.utility.buildURLForTaxonomy
import com.google.gson.Gson
import kotlin.concurrent.thread

class Notebook : AppCompatActivity() {

    lateinit var rvNotebook: RecyclerView
    lateinit var btnNoteAdd: ImageButton
    lateinit var btnNoteReturn: ImageButton
    lateinit var notesAdapter: NotebookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook)

        rvNotebook = findViewById(R.id.rvNotebook)
        btnNoteAdd = findViewById(R.id.ibtnNoteAdd)
        btnNoteReturn = findViewById(R.id.ibtnNoteReturn)


        btnNoteAdd.setOnClickListener {
            Utils.createObservationDialogue(this, ::saveNote)
        }

        btnNoteReturn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    fun saveNote(species: Species) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val noteDao = NoteDao(this)
        thread {
            val user = sharedPreferences.getString(getString(R.string.saved_username_key), "") ?: ""
            val note = Note(species, false)
            val result = noteDao.addNote(user, note)
            runOnUiThread {
                notesAdapter.add(note)
            }
        }
    }
}