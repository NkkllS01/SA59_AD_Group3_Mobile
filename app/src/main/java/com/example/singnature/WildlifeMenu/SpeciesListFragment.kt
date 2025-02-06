package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class SpeciesListFragment : Fragment() {

    private val speciesViewModel: SpeciesViewModel by activityViewModels()
    private lateinit var speciesListView: ListView
    private lateinit var categoryTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_species_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to UI elements
        speciesListView = view.findViewById(R.id.speciesListView)
        categoryTitle = view.findViewById(R.id.categoryTitle)

        // Get the category name from arguments
        val categoryName = arguments?.getString("categoryName") ?: ""

        // Set the title to the selected category
        categoryTitle.text = "$categoryName"

        // Fetch species based on the category name
        speciesViewModel.fetchSpeciesByCategory(categoryName)

        // Observe the species list from the ViewModel
        speciesViewModel.speciesList.observe(viewLifecycleOwner) { speciesList ->
            if (speciesList.isNotEmpty()) {
                // Set the adapter when the species list is updated
                speciesListView.adapter = SpeciesSearchResultsAdapter(requireContext(), speciesList)
            } else {
                speciesListView.adapter = null
            }
        }

        speciesListView.setOnItemClickListener { _, _, position, _ ->
            val speciesList = speciesViewModel.speciesList.value ?: emptyList() // Getting the list from ViewModel

            val selectedSpecies = speciesList[position]
            val bundle = Bundle().apply {
                putInt("specieId", selectedSpecies.specieId) // Pass the selected species ID to the detail fragment
            }
            val speciesDetailFragment = SpeciesDetailFragment()
            speciesDetailFragment.arguments = bundle
            findNavController().navigate(R.id.action_speciesListFragment_to_speciesDetailFragment, bundle)
        }
    }
}
