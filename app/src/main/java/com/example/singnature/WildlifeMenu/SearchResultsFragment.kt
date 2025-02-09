package com.example.singnature.WildlifeMenu

import android.app.ProgressDialog.show
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.singnature.R

class SearchResultsFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = view.findViewById<SearchView>(R.id.search)
        val speciesHeader = view.findViewById<TextView>(R.id.speciesHeader)
        val speciesListView = view.findViewById<ListView>(R.id.speciesListView)
        val sightingsHeader = view.findViewById<TextView>(R.id.sightingsHeader)
        val sightingsListView = view.findViewById<ListView>(R.id.sightingsListView)

        searchViewModel.speciesList.observe(viewLifecycleOwner) { speciesList ->
            if (speciesList.isNotEmpty()) {
                speciesHeader.visibility = View.VISIBLE
                speciesListView.visibility = View.VISIBLE
                speciesListView.adapter = SpeciesSearchResultsAdapter(requireContext(), speciesList)
                println("DEBUG: Updated species list size: ${speciesList.size}")
            } else {
                speciesHeader.visibility = View.GONE
                speciesListView.visibility = View.GONE
                println("DEBUG: No species found.")
            }
        }

        searchViewModel.sightingsList.observe(viewLifecycleOwner) { sightingsList ->
            if (sightingsList.isNotEmpty()) {
                sightingsHeader.visibility = View.VISIBLE
                sightingsListView.visibility = View.VISIBLE
                sightingsListView.adapter = SightingsSearchResultsAdapter(requireContext(), sightingsList)

                sightingsListView.setOnItemClickListener(){_,_,position,_->
                    val sighting =sightingsList[position]
                    val action = SearchResultsFragmentDirections.actionSearchResultFragmentToSightingFragment(sightingId = sighting.sightingId)
                    findNavController().navigate(action)
                }
                println("DEBUG: Updated sightings list size: ${sightingsList.size}")
            } else {
                sightingsHeader.visibility = View.GONE
                sightingsListView.visibility = View.GONE
                println("DEBUG: No sightings found.")
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchViewModel.searchByKeyword(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}