package com.example.singnature.ExploreMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class ParkListFragment : Fragment() {
    private val parkViewModel: ParkViewModel by activityViewModels()
    private lateinit var parkListView: ListView
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_park_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parkListView = view.findViewById(R.id.parkListView)
        navController = findNavController()  // Initialize NavController

        parkViewModel.getAllParks()
        parkViewModel.parkList.observe(viewLifecycleOwner) { parks ->
            val adapter = ParkAdapter(requireContext(), parks, navController)  // Pass NavController to the adapter
            parkListView.adapter = adapter
        }

        // Handle item clicks
       /* parkListView.setOnItemClickListener { _, _, position, _ ->
            val selectedPark = parkViewModel.parkList.value?.get(position)
            val parkId = selectedPark?.parkId ?: return@setOnItemClickListener

            Log.d("ParkListFragment", "Navigating to ParkDetailFragment with parkId: $parkId")

            // Navigate to ParkDetailFragment with parkId
            val action = ParkListFragmentDirections.actionParkListFragmentToParkDetailFragment(parkId)
            navController.navigate(action)
            */

        }
    }

