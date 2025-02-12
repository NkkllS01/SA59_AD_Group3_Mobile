package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
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

        // Get the categoryId and categoryName from arguments
        val categoryId = arguments?.getInt("categoryId") ?: -1  // Default to -1 if null
        val categoryName = arguments?.getString("categoryName") ?: "Unknown Category"

        // Set the title to the selected category
        categoryTitle.text = categoryName

        if (categoryId != -1) {
            // Fetch species based on the categoryId
            speciesViewModel.fetchSpeciesByCategory(categoryId)
        } else {
            Toast.makeText(requireContext(), "Invalid category ID", Toast.LENGTH_SHORT).show()
        }

        // Observe the species list from the ViewModel
        speciesViewModel.speciesList.observe(viewLifecycleOwner) { speciesList ->
            if (speciesList.isNotEmpty()) {
                // Set the adapter when the species list is updated
                speciesListView.adapter =
                    SpeciesSearchResultsAdapter(requireContext(), speciesList) { selectedSpecies ->
                        val action = SpeciesListFragmentDirections
                            .actionSpeciesListFragmentToSpeciesDetailFragment(selectedSpecies.specieId)
                        findNavController().navigate(action)
                    }
            } else {
                speciesListView.adapter = null
            }
        }

        speciesListView.setOnItemClickListener { _, _, position, _ ->
            val speciesList = speciesViewModel.speciesList.value ?: emptyList()
            val selectedSpecies = speciesList[position]

            // Check for null specieId
            val specieId = selectedSpecies.specieId
            if (specieId == null) {
                Toast.makeText(context, "Species ID is missing!", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            // Pass specieId to SpeciesDetailFragment
            val action = SpeciesListFragmentDirections
                .actionSpeciesListFragmentToSpeciesDetailFragment(specieId)
            findNavController().navigate(action)
        }
    }
}