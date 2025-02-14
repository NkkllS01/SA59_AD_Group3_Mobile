package com.example.singnature.WildlifeMenu

import SpeciesCategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.singnature.Network.ApiClient
import com.example.singnature.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpeciesCategoryFragment : Fragment() {
    private lateinit var categoryGridView: GridView
    private lateinit var adapter: SpeciesCategoryAdapter
    private val categories = mutableListOf<SpeciesCategory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_species_category, container, false)
        categoryGridView = view.findViewById(R.id.categoryGridView)

        adapter = SpeciesCategoryAdapter(requireContext(), categories)
        categoryGridView.adapter = adapter

        loadCategories()

        categoryGridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val category = categories[position]
            val action = SpeciesCategoryFragmentDirections
                .actionSpeciesCategoryFragmentToSpeciesListFragment(category.categoryId, category.categoryName)
            findNavController().navigate(action)
        }

        return view
    }

    private fun loadCategories() {
        println("Starting network request...")
        ApiClient.categoryApi.getCategories().enqueue(object : Callback<List<SpeciesCategory>> {
            override fun onResponse(call: Call<List<SpeciesCategory>>, response: Response<List<SpeciesCategory>>) {
                if (response.isSuccessful) {
                    println("Response: ${response.body()}")
                    response.body()?.let {
                        categories.clear()
                        categories.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SpeciesCategory>>, t: Throwable) {
                println("Network Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
