package com.example.singnature.WildlifeMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.singnature.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.navigation.fragment.findNavController

class SightingBottomSheetFragment : BottomSheetDialogFragment() {

    private var sightingId: Int? = null
    private var sightingTitle: String? = null
    private var sightingUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sightingId = it.getInt("sightingId")
            sightingTitle = it.getString("sightingTitle", "Unknown")
            sightingUser = it.getString("sightingUser", "Anonymous")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sighting_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleTextView = view.findViewById<TextView>(R.id.sightingTitle)
        val userTextView = view.findViewById<TextView>(R.id.sightingUser)
        val viewDetailsTextView = view.findViewById<TextView>(R.id.viewDetails)

        titleTextView.text = sightingTitle
        userTextView.text = "Reported by: $sightingUser"

        viewDetailsTextView.setOnClickListener {
            sightingId?.let { id ->
                val action = WildlifeMapsFragmentDirections
                    .actionWildlifeMapsFragmentToSightingFragment(id)
                findNavController().navigate(action)
                dismiss()
            }
        }
    }
}