package com.example.singnature.WildlifeMenu

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.singnature.Network.ApiClient
import com.example.singnature.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapCapabilities
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.singnature.Network.sightingsApiService

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

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)

        val capabilities: MapCapabilities = googleMap.getMapCapabilities()

        val nus_iss = LatLng(1.292385, 103.776643)
        googleMap.addMarker(MarkerOptions().position(nus_iss).title("Marker in NUS-ISS"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nus_iss, 15f))

        fetchSightings(googleMap)
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

    private fun fetchSightings(googleMap: GoogleMap) {
        sightingsApiService.getActiveSightings().enqueue(object : Callback<List<Sightings>> {
            override fun onResponse(call: Call<List<Sightings>>, response: Response<List<Sightings>>) {
                if (response.isSuccessful) {
                    response.body()?.let { sightings ->
                        println("DEBUG: Received ${sightings.size} sightings from API")
                        for (sighting in sightings) {
                            val location = LatLng(sighting.latitude.toDouble(), sighting.longitude.toDouble())
                            println("DEBUG: Adding marker at (${sighting.latitude}, ${sighting.longitude})")

                            googleMap.addMarker(
                                MarkerOptions().position(location).title(sighting.details ?: "Wildlife Sighting")
                            )
                        }
                    }
                } else {
                    println("ERROR: API call was unsuccessful. Response Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Sightings>>, t: Throwable) {
                println("ERROR: Failed to fetch sightings - ${t.message}")
                t.printStackTrace()
            }
        })
    }
}