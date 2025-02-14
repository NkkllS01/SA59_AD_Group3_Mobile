package com.example.singnature.WarningMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.singnature.R

class WarningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val action = WarningFragmentDirections.actionWarningFragmentToWarningListFragment()
        findNavController().navigate(action)
        return inflater.inflate(R.layout.fragment_warning, container, false)
    }
}
