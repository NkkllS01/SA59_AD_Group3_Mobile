package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class SpeciesCategoryFragment : Fragment() {
    private lateinit var categoryGridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_species_category, container, false)
        categoryGridView = view.findViewById(R.id.categoryGridView)

        val categories = listOf(
            SpeciesCategory("Bees", R.drawable.image_placeholder),
            SpeciesCategory("Butterflies", R.drawable.image_placeholder),
            SpeciesCategory("Birds", R.drawable.image_placeholder)
        )

        val adapter = SpeciesCategoryAdapter(requireContext(), categories)
        categoryGridView.adapter = adapter

        categoryGridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val category = categories[position]
            val action = SpeciesCategoryFragmentDirections
                .actionSpeciesCategoryFragmentToSpeciesListFragment(category.name)
            findNavController().navigate(action)
        }

        return view
    }
}
