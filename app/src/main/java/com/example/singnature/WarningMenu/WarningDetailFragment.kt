package com.example.singnature.WarningMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.singnature.R

class WarningDetailFragment : Fragment() {
    private val warningViewModel: WarningViewModel by activityViewModels()

    private lateinit var descriptionTextView: TextView

    private var warningId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_warning_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        descriptionTextView = view.findViewById(R.id.warningDescription)

        val args = WarningDetailFragmentArgs.fromBundle(requireArguments())
        warningId = args.warningId

        warningViewModel.fetchWarningDetail(warningId)

        warningViewModel.warningDetail.observe(viewLifecycleOwner) { warning ->
            descriptionTextView.text = warning.description

        }
    }
}
