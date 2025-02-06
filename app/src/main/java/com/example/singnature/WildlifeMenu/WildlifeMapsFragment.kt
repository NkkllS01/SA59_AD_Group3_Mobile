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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.singnature.Network.ApiClient
import com.example.singnature.Network.speciesApiService
import com.example.singnature.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.singnature.Network.sightingsApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class WildlifeMapsFragment : Fragment(){

    private lateinit var searchView: SearchView
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

        googleMap.setOnMapClickListener { latLng ->googleMap
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng), 500, null)
        }
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

        searchView = view.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchByKeyword(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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
        if (!::clusterManager.isInitialized) {
            clusterManager = ClusterManager(requireContext(), googleMap)
            clusterManager.renderer = CustomClusterRenderer(requireContext(), googleMap, clusterManager)
        } else {
            clusterManager.clearItems()
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        addSightingsMarkers(googleMap)

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

    private fun searchByKeyword(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val speciesResponse = speciesApiService.searchSpeciesByKeyword(keyword).execute()
                val sightingsResponse = sightingsApiService.searchSightingsByKeyword(keyword).execute()

                if (speciesResponse.isSuccessful && sightingsResponse.isSuccessful) {
                    val speciesList = speciesResponse.body() ?: emptyList()
                    val sightingsList = sightingsResponse.body() ?: emptyList()

                    withContext(Dispatchers.Main) {
                        val action = WildlifeMapsFragmentDirections
                            .actionWildlifeMapsFragmentToSearchResultsFragment(
                                keyword,
                                speciesList.toTypedArray(),
                                sightingsList.toTypedArray()
                            )
                        findNavController().navigate(action)
                    }
                } else {
                    println("ERROR: API call failed with response codes: Species ${speciesResponse.code()}, Sightings ${sightingsResponse.code()}")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to fetch search results - ${e.message}")
                e.printStackTrace()
            }
        }
    }
    private fun addSightingsMarkers(googleMap: GoogleMap) {
        // 新加坡的经纬度范围
        val singaporeBounds = LatLngBounds(
            LatLng(1.2500, 103.5700), // 西南角
            LatLng(1.4700, 104.1000)  // 东北角
        )

        // 创建 Sightings 对象的列表
        val sightingsList = listOf(
            Sightings(
                sightingId = 1,
                userId = 101,
                userName = "Alice",
                date = Date(),
                specieId = 201,
                specieName = "Lion",
                details = "Spotted near the river",
                imageUrl = "http://example.com/lion.jpg",
                latitude = 1.3521,  // 新加坡的经纬度示例
                longitude = 103.8198,
                status = "Active"
            ),
            Sightings(
                sightingId = 2,
                userId = 102,
                userName = "Bob",
                date = Date(),
                specieId = 202,
                specieName = "Elephant",
                details = "Feeding on grass",
                imageUrl = "http://example.com/elephant.jpg",
                latitude = 1.2900,  // 新加坡的经纬度示例
                longitude = 103.8500,
                status = "Active"
            ),
            // 添加其他 Sightings 对象，确保它们在新加坡范围内
        )

        // 清空现有的标记（如果需要）
        clusterManager.clearItems()

        // 将 Sightings 对象添加到 ClusterManager
        sightingsList.forEach { sighting ->
            if (singaporeBounds.contains(sighting.getPosition())) {
                clusterManager.addItem(sighting)
            }
        }

        // 刷新聚类
        clusterManager.cluster()
    }
}