package com.example.birdy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

class ObservationAdapter(private val sightings: List<Sighting>): RecyclerView.Adapter<ObservationAdapter.ViewHolder>(), Filterable {
    private val completeSightings = sightings
    private var filteredSightings = sightings

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvCode: TextView
        val tvDate: TextView
        val tvCoords: TextView

        init {
            tvCode = view.findViewById(R.id.tvSightingCode)
            tvDate = view.findViewById(R.id.tvSightingDate)
            tvCoords = view.findViewById(R.id.tvSightingCoords)
        }
    }

    // Create new views, called by layout manager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sighting_item, parent, false)
        return ViewHolder(view)
    }

    // Replace content of a view, called by layout manager
    override fun onBindViewHolder(holder: ObservationAdapter.ViewHolder, position: Int) {
        val sighting = filteredSightings[position]
        holder.tvCode.text = sighting.species
        val df = DateFormat.getDateInstance()
        holder.tvDate.text = df.format(sighting.date)
        val coords = "Lat: ${sighting.lat} | Lng: ${sighting.lng}"
        holder.tvCoords.text = coords
    }

    override fun getItemCount(): Int {
        return filteredSightings.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charSequenceString = constraint.toString()
                if (charSequenceString.isEmpty()) {
                    filteredSightings = completeSightings
                } else {
                    val filteredList: MutableList<Sighting> = ArrayList()
                    for (sighting in completeSightings) {
                        if (sighting.species.lowercase()
                                .contains(charSequenceString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(sighting)
                        }
                        filteredSightings = filteredList
                    }
                }
                val results = FilterResults()
                results.values = filteredSightings
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredSightings = results.values as List<Sighting>
                notifyDataSetChanged()
            }
        }
    }
}