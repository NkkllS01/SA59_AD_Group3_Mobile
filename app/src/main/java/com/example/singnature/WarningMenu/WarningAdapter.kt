package com.example.singnature.WarningMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.singnature.R

class WarningAdapter(
    private val context: Context,
    private val warnings: List<Warning>,
    private val listener: OnWarningClickListener
) : BaseAdapter() {

    override fun getCount(): Int = warnings.size
    override fun getItem(position: Int): Warning = warnings[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_warning, parent, false)

        val warning = getItem(position)

        // Bind UI elements
        val dateTextView = view.findViewById<TextView>(R.id.date)
        val speciesOrTierTextView = view.findViewById<TextView>(R.id.warningSpecies_Tier)
        val descriptionTextView = view.findViewById<TextView>(R.id.warningDescription)

        // Set data
        dateTextView.text = warning.date ?: "Unknown Date"
        descriptionTextView.text = warning.description ?: "No description available"

        when (warning.source) {
            "SIGHTING" -> {
                speciesOrTierTextView.text = "Specie: ${warning.specie ?: "Unknown"}"
            }
            "DENGUE" -> {
                speciesOrTierTextView.text = "Tier: ${warning.alertLevel ?: "N/A"}"
            }
            else -> {
                speciesOrTierTextView.text = "Warning"
            }
        }
        view.setOnClickListener {
            listener.onWarningClicked(warning.warningId, warning.source)
        }
        return view
    }
}
