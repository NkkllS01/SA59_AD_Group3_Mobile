package com.example.singnature.WildlifeMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.singnature.R

class SearchResultsFragment : Fragment() {

    private val args: SearchResultsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val speciesList = args.speciesList.toList()
        val sightingsList = args.sightingsList.toList()

        val speciesHeader = view.findViewById<TextView>(R.id.speciesHeader)
        val speciesListView = view.findViewById<ListView>(R.id.speciesListView)
        val sightingsHeader = view.findViewById<TextView>(R.id.sightingsHeader)
        val sightingsListView = view.findViewById<ListView>(R.id.sightingsListView)

        if (speciesList.isNotEmpty()) {
            speciesHeader.visibility = View.VISIBLE
            speciesListView.visibility = View.VISIBLE
            speciesListView.adapter = SpeciesSearchResultsAdapter(requireContext(), speciesList)
        }

        if (sightingsList.isNotEmpty()) {
            sightingsHeader.visibility = View.VISIBLE
            sightingsListView.visibility = View.VISIBLE
            sightingsListView.adapter = SightingsSearchResultsAdapter(requireContext(), sightingsList)
        }
    }
}