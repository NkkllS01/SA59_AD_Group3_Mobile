package com.example.singnature.WildlifeMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.singnature.R

class SpeciesDetailFragment : Fragment() {

    private val speciesViewModel: SpeciesViewModel by activityViewModels()

    private lateinit var specieNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var highlightsTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var speciesImageView: ImageView

    private var specieId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_species_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specieNameTextView = view.findViewById(R.id.speciesName)
        descriptionTextView = view.findViewById(R.id.speciesDescription)
        highlightsTextView = view.findViewById(R.id.speciesHighlight)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        speciesImageView = view.findViewById(R.id.speciesImage)

        val args = SpeciesDetailFragmentArgs.fromBundle(requireArguments())
        specieId = args.specieId

        if (specieId == -1) {
            Toast.makeText(context, "Species ID not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        speciesViewModel.fetchSpeciesDetail(specieId)

        speciesViewModel.speciesDetail.observe(viewLifecycleOwner) { specie ->
            specie?.let {
                specieNameTextView.text = it.specieName
                descriptionTextView.text = it.description
                highlightsTextView.text = it.highlights
                loadingProgressBar.visibility = View.GONE

                Glide.with(this)
                    .load(it.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(speciesImageView)
            }
        }
    }
}
