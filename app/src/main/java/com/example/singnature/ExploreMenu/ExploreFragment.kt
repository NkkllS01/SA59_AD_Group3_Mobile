package com.example.singnature.ExploreMenu

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.singnature.ExploreMenu.Achievement.AchievementActivity
import com.example.singnature.R
import org.jline.terminal.MouseEvent

class ExploreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        // Find the button to trigger navigation
        val navigateButton: Button = view.findViewById(R.id.btnNavigateToAchievement)

        // Set onClickListener to navigate to AchievementActivity
        navigateButton.setOnClickListener {
            val intent = Intent(requireContext(), AchievementActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}