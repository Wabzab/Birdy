package com.example.birdy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class ViewObservations : AppCompatActivity() {

    // Declaring previousCardViewId var to keep track of the previous Card View's ID
    private var previousCardViewId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_observations)

        val sightingDAO = SightingDAO(this)
        val sightings = sightingDAO.getSightings()
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ViewObservationsPage)

        // LayoutInflater for inflating cardview_species.xml
        val inflater = LayoutInflater.from(this)

        for (sighting in sightings) {
            // Inflate the cardview_species.xml layout
            val cardView = inflater.inflate(R.layout.cardview_species, null) as CardView

            // Set a unique ID for each CardView
            cardView.id = View.generateViewId()

            // Find the TextView inside the CardView
            val speciesTextView = cardView.findViewById<TextView>(R.id.speciesTextView)
            val GreyLocationIcon = cardView.findViewById<ImageView>(R.id.GreyLocationIcon)
            val InnerCardView = cardView.findViewById<CardView>(R.id.InnerCardView)
            val InnerBirdPic= cardView.findViewById<ImageView>(R.id.InnerBirdPic)
            val TextViewCoordinates = cardView.findViewById<TextView>(R.id.TextViewCoordinates)
            val TextViewDescription = cardView.findViewById<TextView>(R.id.TextViewDescription)

            // Set the species name
            speciesTextView.text = sighting.speciesCode

            // Set layout parameters for the CardView
            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            // Set topToBottom property for the CardView
            if (previousCardViewId != 0) {
                params.topToBottom = previousCardViewId
            }

            cardView.layoutParams = params

            // Add the CardView to the ConstraintLayout
            constraintLayout.addView(cardView)

            // Update previousCardViewId to this CardView's ID
            previousCardViewId = cardView.id
        }
    }
}