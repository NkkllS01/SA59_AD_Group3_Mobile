package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.text.Highlights
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.singnature.R

class SpeciesDetailFragment : Fragment() {
    private lateinit var speciesImage: ImageView
    private lateinit var speciesName: TextView
    private lateinit var speciesDescription: TextView
    private lateinit var speciesHighlight: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_species_detail, container, false)

        speciesImage = view.findViewById(R.id.speciesImage)
        speciesName = view.findViewById(R.id.speciesName)
        speciesDescription = view.findViewById(R.id.speciesDescription)
        speciesHighlight = view.findViewById(R.id.speciesHighlight)

        val speciesId = arguments?.getInt("speciesId") ?: 0

        // Dummy data (replace with API call)
        speciesName.text = "Honey Bee"
        speciesDescription.text = "The honey bee is a social insect known for producing honey."
        speciesImage.setImageResource(R.drawable.image_placeholder)

        return view
    }
}
