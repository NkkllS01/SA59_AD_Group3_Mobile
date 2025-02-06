package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class SpeciesListFragment : Fragment() {
    private lateinit var speciesListView: ListView
    private lateinit var categoryTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_species_list, container, false)
        speciesListView = view.findViewById(R.id.speciesListView)
        categoryTitle = view.findViewById(R.id.categoryTitle)

        val categoryName = arguments?.getString("categoryName") ?: "Unknown"
        categoryTitle.text = "$categoryName Species"

        // Dummy data (replace with API data)
        val speciesList = listOf(
            Species(1, "Honey Bee", ",d","dw"),
            Species(2, "Bumble Bee","dw","qwd"),
            Species(3, "Carpenter Bee","wdq","dw")
        )

        val adapter = SpeciesSearchResultsAdapter(requireContext(), speciesList)
        speciesListView.adapter = adapter

        speciesListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedSpecies = speciesList[position]
            val action = SpeciesListFragmentDirections
                .actionSpeciesListFragmentToSpeciesDetailFragment(selectedSpecies.specieId)
            findNavController().navigate(action)
        }

        return view
    }
}
