package com.example.singnature.WildlifeMenu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.singnature.R

class SpeciesSearchResultsAdapter (
    private val context: Context,
    private val data: List<Species>,
    private val onItemClick: (Species) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Species = data[position]
    override fun getItemId(position: Int): Long = data[position].specieId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.itemImage)
        val textView = view.findViewById<TextView>(R.id.itemText)
        val reportByText = view.findViewById<TextView>(R.id.reportByText)

        val item = getItem(position)

        textView.text = item.specieName
        reportByText.visibility = View.GONE

        Log.d("SpeciesAdapter", "Image URL: ${item.imageUrl}")

        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.imageUrl)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(imageView)
        } else {
            Log.d("SpeciesAdapter", "Using placeholder for Specie ID: ${item.specieId}")
            imageView.setImageResource(R.drawable.image_placeholder)
        }

        view.setOnClickListener {
            Log.d("SpeciesAdapter", "Item clicked: ${item.specieId}")
            onItemClick(item)
        }

        return view
    }
}