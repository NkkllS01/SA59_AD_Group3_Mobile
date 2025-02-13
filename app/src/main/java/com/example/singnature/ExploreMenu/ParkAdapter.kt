package com.example.singnature.ExploreMenu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import com.example.singnature.R

class ParkAdapter(
    private val context: Context,
    private val parks: List<Park>,
    private val navController: NavController // Add NavController as a parameter
) : BaseAdapter() {
    override fun getCount(): Int = parks.size
    override fun getItem(position: Int): Park = parks[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_park, parent, false)
        val park = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.parkName)
        val regionTextView = view.findViewById<TextView>(R.id.parkRegion)
        val navIcon = view.findViewById<ImageView>(R.id.navigationIcon)

        nameTextView.text = park.parkName
        regionTextView.text = park.parkRegion

        view.setOnClickListener {
            Log.d("ParkAdapter", "Clicked park ${park.parkId}")
            val action =
                ParkListFragmentDirections.actionParkListFragmentToParkDetailFragment(park.parkId)
            navController.navigate(action)
        }
        // Set up click listener for the navigation icon (opens Google Maps within the app)
        navIcon.setOnClickListener {
            val action = ParkListFragmentDirections.actionParkListFragmentToParkMapFragment(park)
            navController.navigate(action)  // Use the passed NavController
        }

        return view
    }
}
