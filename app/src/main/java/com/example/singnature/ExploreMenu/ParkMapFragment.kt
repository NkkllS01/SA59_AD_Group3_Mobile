package com.example.singnature.ExploreMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.singnature.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class ParkMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var park: Park

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        park = ParkMapFragmentArgs.fromBundle(requireArguments()).park
        return inflater.inflate(R.layout.fragment_park_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())

        val parkLocation = LatLng(park.latitude, park.longitude)
        mMap.addMarker(MarkerOptions()
            .position(parkLocation)
            .title(park.parkName)
            .snippet("Tap for more details")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLocation, 15f))
    }

    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            val view = layoutInflater.inflate(R.layout.fragment_info_window, null)

            val parkNameTextView = view.findViewById<TextView>(R.id.parkNameText)
            val addressTextView = view.findViewById<TextView>(R.id.parkAddressText)
            val openingHoursTextView = view.findViewById<TextView>(R.id.parkOpeningHoursText)

            parkNameTextView.text = park.parkName
            addressTextView.text = "📍 ${park.parkDescription}"
            openingHoursTextView.text = "🕒 ${park.openingHours}"

            return view
        }
    }
}
