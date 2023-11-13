package com.example.birdy.notebook

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.birdy.R

class NotebookAdapter(private val notes: List<Note>): RecyclerView.Adapter<NotebookAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView
        val btnSave: ImageButton
        val btnDelete: ImageButton

        init {
            tvName = view.findViewById(R.id.tvNoteSpecies)
            btnSave = view.findViewById(R.id.ibtnNoteSave)
            btnDelete = view.findViewById(R.id.ibtnNoteDelete)
        }
    }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notebook_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.tvName.text = note.species.common_name
        if (note.checked) {
            holder.tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}