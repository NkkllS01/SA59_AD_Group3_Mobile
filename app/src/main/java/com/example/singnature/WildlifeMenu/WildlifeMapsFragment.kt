package com.example.singnature.WildlifeMenu

import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WildlifeMapsFragment : Fragment() {

    private val viewModel: WildlifeMapViewModel by activityViewModels()

    private var currentLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 1001
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var loadingLogo: ImageView
    private lateinit var clusterManager: ClusterManager<Sightings>

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.setAllGesturesEnabled(true)

        enableMyLocation(googleMap)

        currentLocation?.let {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } ?: println("WARNING: Location not available yet.")

        setupClusterManager(googleMap)
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
        if (viewModel.hasLogoBeenShown) {
            loadingLogo.visibility = View.GONE
        } else {
            loadingLogo.visibility = View.VISIBLE
            view.postDelayed({
                loadingLogo.visibility = View.GONE
                viewModel.hasLogoBeenShown = true
            }, 3000)
        }

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

        val searchView: SearchView = requireView().findViewById(R.id.search)
        searchView.visibility = View.VISIBLE
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

    private fun setupClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager(requireContext(), googleMap)
        clusterManager.renderer = CustomClusterRenderer(requireContext(), googleMap, clusterManager)

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener { cluster ->
            val builder = LatLngBounds.Builder()

            for (item in cluster.items) {
                builder.include(item.position)
            }

            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            true
        }

        clusterManager.setOnClusterItemClickListener { item ->
            println("DEBUG: Individual item clicked: ${item.details}")
            false
        }

        fetchSightings()
    }

    private fun fetchSightings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = sightingsApiService.getActiveSightings().execute()
                if (response.isSuccessful) {
                    response.body()?.let { sightings ->
                        withContext(Dispatchers.Main) {
                            clusterManager.clearItems()
                            clusterManager.addItems(sightings)
                            clusterManager.cluster()
                            println("DEBUG: Added ${sightings.size} sightings to cluster")
                        }
                    }
                } else {
                    println("ERROR: API call was unsuccessful. Response Code: ${response.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to fetch sightings - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}