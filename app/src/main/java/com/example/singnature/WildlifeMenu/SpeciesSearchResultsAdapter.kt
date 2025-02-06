package com.example.singnature.WildlifeMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.singnature.R

class SpeciesSearchResultsAdapter (
    private val context: Context,
    private val data: List<Species>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Species = data[position]
    override fun getItemId(position: Int): Long = data[position].SpecieId.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.itemImage)
        val textView = view.findViewById<TextView>(R.id.itemText)

        val item = getItem(position)

        textView.text = item.SpecieName
        imageView.setImageResource(R.drawable.image_placeholder)

        return view
    }
}