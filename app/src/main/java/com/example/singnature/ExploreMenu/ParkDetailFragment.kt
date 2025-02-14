package com.example.singnature.ExploreMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.singnature.R

class ParkDetailFragment : Fragment() {
    private val parkViewModel: ParkViewModel by activityViewModels()

    private lateinit var parkImageView: ImageView
    private lateinit var parkNameTextView: TextView
    private lateinit var openingHoursTextView: TextView
    private lateinit var descriptionTextView: TextView

    private var parkId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_park_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        parkImageView = view.findViewById(R.id.parkImage)
        parkNameTextView = view.findViewById(R.id.parkName)
        descriptionTextView = view.findViewById(R.id.parkDescription)
        openingHoursTextView = view.findViewById(R.id.openingHours)

        val args = ParkDetailFragmentArgs.fromBundle(requireArguments())
        parkId = args.parkId

        parkViewModel.fetchParkDetail(parkId)

        parkViewModel.parkDetail.observe(viewLifecycleOwner) { park ->
            parkNameTextView.text = park.parkName
            descriptionTextView.text = park.parkDescription
            openingHoursTextView.text = park.openingHours

            // 加载图片
            Glide.with(this)
                .load(park.imageUrl) // 这里是你的图片 URL
                .into(parkImageView)
        }
    }
}
