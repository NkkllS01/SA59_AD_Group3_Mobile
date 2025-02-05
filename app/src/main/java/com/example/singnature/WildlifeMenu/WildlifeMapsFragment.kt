package com.example.singnature.WildlifeMenu

import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.example.singnature.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.singnature.Network.sightingsApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class WildlifeMapsFragment : Fragment() {

    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 1001
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var loadingLogo: ImageView

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)

        enableMyLocation(googleMap)

        currentLocation?.let {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } ?: println("WARNING: Location not available yet.")

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

        loadingLogo = view.findViewById(R.id.loadingLogo)
        loadingLogo.visibility = View.VISIBLE

        mapFragment = childFragmentManager.findFragmentById(R.id.wildlifeMapsFragment) as SupportMapFragment
        mapFragment.view?.visibility = View.GONE

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        getCurrentLocationUser()
    }

    private fun getCurrentLocationUser() {
        if(ActivityCompat.checkSelfPermission(
                requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode)
            return
        }

        fusedLocationProviderClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener {
            location ->
            if(location != null) {
                currentLocation = location
                initMap()
            } else {
                println("ERROR: Could not retrieve location")
            }
        }
    }

    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.wildlifeMapsFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        loadingLogo.visibility = View.GONE
        mapFragment?.view?.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == permissionCode && grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationUser()
            } else {
                println("ERROR: Location permission denied")
            }
        }

    private fun enableMyLocation(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
        }
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