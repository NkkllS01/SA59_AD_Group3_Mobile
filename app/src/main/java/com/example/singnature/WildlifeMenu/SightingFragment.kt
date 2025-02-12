package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.singnature.Network.sightingsApiService
import com.example.singnature.R
import com.example.singnature.databinding.FragmentSightingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SightingFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding : FragmentSightingBinding
    private var sighting: Sightings? = null
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var sightingId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSightingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // call interface to retrieve sighting object
        arguments.let{
            sightingId = it?.let { it1 -> SightingFragmentArgs.fromBundle(it1).sightingId }
        }
        sightingId?.let { getSightingById(it) }
    }

    private fun getSightingById(id: Int) {
        val call = sightingsApiService.getSightingById(id)

        call.enqueue(object : Callback<Sightings> {
            override fun onResponse(call: Call<Sightings>, response: Response<Sightings>) {
                if (response.isSuccessful) {
                    sighting = response.body()
                    updateUI()
                } else {
                    handleError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Sightings>, t: Throwable) {
                handleError("Network error: ${t.message}")
            }
        })

    }
    private fun handleError(errorMessage: String) {
        binding.apply{
            titleTextView.visibility = View.GONE
            sightingImageView.visibility = View.GONE
            mapView.visibility = View.GONE
            reportedByTextView.visibility = View.GONE
            userNameTextView.visibility = View.GONE
            speciesTextView.visibility = View.GONE
            speciesNameTextView.visibility = View.GONE
            dateTextView.visibility = View.GONE
            sightingDateTextView.visibility = View.GONE
            detailsTextView.visibility = View.GONE
            sightingDetailsTextView.visibility = View.GONE

            errorTextView.text = errorMessage
            errorTextView.visibility = View.VISIBLE
        }
    }
    private fun updateUI() {
        sighting?.let {
            binding.apply{
                titleTextView.text = "Sighting Details"
                reportedByTextView.text = "Reported By:"
                userNameTextView.text = it.userName

                dateTextView.text = "Date:"
                sightingDateTextView.text = it.date.toString()

                speciesTextView.text = "Species name:"
                speciesNameTextView.text = it.specieName

                detailsTextView.text = "Details:"
                sightingDetailsTextView.text = it.details }

            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.sightingImageView)

           moveMapToLocation()
        }
    }

    private fun moveMapToLocation() {
        sighting?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap=map

        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = true
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}