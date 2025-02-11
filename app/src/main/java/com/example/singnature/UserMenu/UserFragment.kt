package com.example.singnature.UserMenu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.singnature.Network.ApiClient
import com.example.singnature.databinding.FragmentUserBinding
import com.example.singnature.Network.AuthService
import com.example.singnature.Network.UpdateProfileRequest
import com.example.singnature.R
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private lateinit var authService: AuthService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)

        sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        authService = ApiClient.authService

        loadUserProfile()
        setupListeners()

        return binding.root
    }


    private fun loadUserProfile() {
        val username = sharedPref.getString("username", "") ?: ""
        val email = sharedPref.getString("email", "") ?: ""
        val phone = sharedPref.getString("phone", "") ?: ""
        val subscribeWarning = sharedPref.getBoolean("subscribeWarning", false)
        val subscribeNewsletter = sharedPref.getBoolean("subscribeNewsletter", false)

        println("DEBUG: username=$username, email=$email, phone=$phone, subscribeWarning=$subscribeWarning, subscribeNewsletter=$subscribeNewsletter")

        binding.apply {
            usernameEditText.setText(username)
            emailEditText.setText(email)
            phoneEditText.setText(phone)
            subscribeWarningSwitch.isChecked = subscribeWarning
            subscribeNewsletterSwitch.isChecked = subscribeNewsletter
        }
    }



    private fun setupListeners() {
        binding.apply {
            saveButton.setOnClickListener {
                val userId = sharedPref.getInt("userId", -1) // 确保拿到 userId
                if (userId == -1) {
                    showToast("User ID not found, please login again.")
                    return@setOnClickListener
                }

                val email = emailEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val subscribeWarning = subscribeWarningSwitch.isChecked
                val subscribeNewsletter = subscribeNewsletterSwitch.isChecked

                val updateRequest = UpdateProfileRequest(userId, email, phone, subscribeWarning, subscribeNewsletter)
                updateUserProfile(updateRequest)
            }

            logoutButton.setOnClickListener {
                logoutUser()
            }
        }
    }



    private fun updateUserProfile(request: UpdateProfileRequest) {
        authService.updateProfile(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    saveProfileLocally(request)
                    showToast("Profile updated successfully")
                } else {
                    showToast("Failed to update profile")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }


    private fun saveProfileLocally(request: UpdateProfileRequest) {
        with(sharedPref.edit()) {
            request.email?.let { putString("email", it) }
            request.phone?.let { putString("phone", it) }
            putBoolean("subscribeWarning", request.subscribeWarning)
            putBoolean("subscribeNewsletter", request.subscribeNewsletter)
            apply()
        }
    }


    private fun logoutUser() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        showToast("Logged out successfully")

        val navController = requireActivity().findNavController(R.id.fragment_container_view)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        navController.navigate(R.id.loginFragment, null, navOptions)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
