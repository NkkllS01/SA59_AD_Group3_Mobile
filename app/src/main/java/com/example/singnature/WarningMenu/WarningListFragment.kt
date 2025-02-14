package com.example.singnature.WarningMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class WarningListFragment : Fragment(), OnWarningClickListener {

    private val warningViewModel: WarningViewModel by activityViewModels()
    private lateinit var warningListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_warning_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        warningListView = view.findViewById(R.id.warningListView)

        warningViewModel.getAllWarnings()

        warningViewModel.warningList.observe(viewLifecycleOwner) { warningList ->
            if (warningList.isNotEmpty()) {
                // Set up the adapter with the listener
                val adapter = WarningAdapter(
                    requireContext(),
                    warningList,
                    this // Passing this fragment as the listener
                )
                warningListView.adapter = adapter
            }
        }
    }

    override fun onWarningClicked(warningId: Int, type: String) {
        when (type) {
            "SIGHTING" -> {
                val action = WarningListFragmentDirections
                    .actionWarningListFragmentToSightingFragment(warningId)
                findNavController().navigate(action)
            }
            "DENGUE" -> {
                    // Optionally show a toast message for Dengue
                    Toast.makeText(context, "Dengue alert!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

interface OnWarningClickListener {
    fun onWarningClicked(warningId: Int, type: String)
}
