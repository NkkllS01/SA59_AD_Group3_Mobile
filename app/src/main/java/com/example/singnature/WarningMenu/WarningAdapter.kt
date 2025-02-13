package com.example.singnature.WarningMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.singnature.Network.sightingsApiService
import com.example.singnature.R
import com.example.singnature.WildlifeMenu.Sightings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WarningAdapter(
    private val context: Context,
    private val warnings: List<Warning>,
    private val listener: OnWarningClickListener
) : BaseAdapter() {

    // Cache to store fetched species names
    private val specieNameCache = mutableMapOf<Int, String>()

    override fun getCount(): Int = warnings.size
    override fun getItem(position: Int): Warning = warnings[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_warning, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val warning = getItem(position)

        // Set initial placeholder or loading text
        viewHolder.speciesOrTierTextView.text = "Loading..."

        // Check the source of the warning
        when (warning.source) {
            "DENGUE" -> {
                // If the source is "DENGUE", use tier (alertLevel)
                viewHolder.speciesOrTierTextView.text = "Tier: ${warning.alertLevel ?: "N/A"}"
            }
            "SIGHTING" -> {
                // If the source is "SIGHTING", check if the specieName is cached or make the API call
                val sightingId = warning.sightingId ?: return view.apply {
                    viewHolder.speciesOrTierTextView.text = "Specie: Unknown"
                }

                // Check if the specieName is already cached
                val cachedSpecieName = specieNameCache[sightingId]
                if (cachedSpecieName != null) {
                    // If the specieName is cached, set it directly
                    viewHolder.speciesOrTierTextView.text = "Specie: $cachedSpecieName"
                } else {
                    // Otherwise, make the API call to fetch the specieName
                    sightingsApiService.getSightingById(sightingId).enqueue(object : Callback<Sightings> {
                        override fun onResponse(call: Call<Sightings>, response: Response<Sightings>) {
                            if (response.isSuccessful) {
                                val sighting = response.body()
                                val speciesName = sighting?.specieName ?: "Unknown"

                                // Cache the result
                                specieNameCache[sightingId] = speciesName
                                // Update the TextView with the fetched specieName
                                viewHolder.speciesOrTierTextView.text = "Specie: $speciesName"
                            } else {
                                // If the API call fails, show default text
                                viewHolder.speciesOrTierTextView.text = "Specie: Unknown"
                            }
                        }

                        override fun onFailure(call: Call<Sightings>, t: Throwable) {
                            // If the API call fails, show default text
                            viewHolder.speciesOrTierTextView.text = "Specie: Unknown"
                        }
                    })
                }
            }
            else -> {
                // If the source is neither "DENGUE" nor "SIGHTING", display "Unknown"
                viewHolder.speciesOrTierTextView.text = "Warning"
            }
        }

        // Handle click event
        view.setOnClickListener {
            listener.onWarningClicked(warning.warningId, warning.source)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val speciesOrTierTextView: TextView = view.findViewById(R.id.warningSpecies_Tier)
    }
}
