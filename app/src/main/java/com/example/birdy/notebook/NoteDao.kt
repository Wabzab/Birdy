package com.example.birdy.notebook

import android.app.Activity
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.birdy.accounts.User
import com.example.birdy.observations.Species
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteDao(activity: Activity) {

    private val db = Firebase.firestore
    private var sharedPreferences: SharedPreferences
    private val activity: Activity

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        this.activity = activity
    }

    fun fetchNotes(user: String): ArrayList<Note> {
        val fetchTask = db.collection("notes").document(user).get()
        val userNotes = kotlin.runCatching { Tasks.await(fetchTask) }.getOrNull() ?: return ArrayList()
        if (userNotes.exists()) {
            val notes = ArrayList<Note>()
            for (note in userNotes.get("notes") as List<Map<String, Any>>) {
                val data = note["species"] as HashMap<String, Any>
                notes.add( Note(
                    Species(
                        data.get("scientific_name") as String,
                        data.get("common_name") as String,
                        data.get("species_code") as String,
                        data.get("category") as String,
                        data.get("taxon_order") as String,
                        data.get("com_name_codes") as List<String>,
                        data.get("sci_name_codes") as  List<String>,
                        data.get("banding_codes") as List<String>,
                        data.get("order") as String,
                        data.get("family_code") as String,
                        data.get("family_com_name") as String,
                        data.get("family_sci_name") as String
                    ),
                    note["checked"] as Boolean
                ))
            }
            return notes
        } else {
            return ArrayList()
        }
    }

    fun addNote(user: String, note: Note): Boolean {
        val notesDoc = db.collection("notes").document(user)
        val fetchTask = notesDoc.get()
        val userNotes = kotlin.runCatching { Tasks.await(fetchTask) }.getOrNull() ?: return false
        if (!userNotes.exists()) {
            val setTask = notesDoc.set(
                note
            )
            val result = kotlin.runCatching { Tasks.await(setTask) }
            return result.isSuccess
        }
        val updateTask = notesDoc.update("notes", FieldValue.arrayUnion(note))
        val result = kotlin.runCatching { Tasks.await(updateTask) }
        return result.isSuccess
    }

    fun removeNote(user: String, note: Note): Boolean {
        val notesDoc = db.collection("notes").document(user)
        val deleteTask = notesDoc.update("notes", FieldValue.arrayRemove(note))
        val result = kotlin.runCatching { Tasks.await(deleteTask) }
        return result.isSuccess
    }

    fun checkNote(user: String, note: Note): Boolean {
        val notesDoc = db.collection("notes").document(user)
        removeNote(user, note)
        note.checked = true
        val checkTask = notesDoc.update("notes", FieldValue.arrayUnion(note))
        val result = kotlin.runCatching { Tasks.await(checkTask) }
        return result.isSuccess
    }
}