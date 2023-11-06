package com.example.birdy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SpeciesAdapter(private val species: Array<Species>): RecyclerView.Adapter<SpeciesAdapter.ViewHolder>() {

    // Reference to the type of views being used by custom view-holder
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvCommonName: TextView

        init {
            tvCommonName = view.findViewById(R.id.tvCommonName)
        }
    }

    // Create new views, called by layout manager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.species_item, parent, false)
        return ViewHolder(view)
    }

    // Replace content of a view, called by layout manager
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCommonName.text = species[position].common_name
    }

    override fun getItemCount(): Int {
        return species.size
    }

}