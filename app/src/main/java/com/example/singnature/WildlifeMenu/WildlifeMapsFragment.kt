package com.example.singnature.WildlifeMenu

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.singnature.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class WildlifeMapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val nus_iss = LatLng(1.29245, 103.77657)
        googleMap.addMarker(MarkerOptions().position(nus_iss).title("Marker in NUS-ISS"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nus_iss, 15f))

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wildlife_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.wildlifeMapsFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}