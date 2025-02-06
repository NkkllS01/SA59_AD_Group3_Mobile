package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.singnature.R

class SpeciesDetailFragment : Fragment() {

    private val speciesViewModel: SpeciesViewModel by activityViewModels()

    private lateinit var specieNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var highlightsTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    private var specieId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_species_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specieNameTextView = view.findViewById(R.id.speciesName)
        descriptionTextView = view.findViewById(R.id.speciesDescription)
        highlightsTextView = view.findViewById(R.id.speciesHighlight)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        specieId = arguments?.getInt("specieId")

        // Fetch species details
        specieId?.let {
            speciesViewModel.fetchSpeciesDetail(it)
        }

        // Observe species detail
        speciesViewModel.speciesDetail.observe(viewLifecycleOwner) { specie ->
            specie?.let {
                specieNameTextView.text = it.specieName
                descriptionTextView.text = it.description
                highlightsTextView.text = it.highlights
                loadingProgressBar.visibility = View.GONE
            }
        }
    }
}

