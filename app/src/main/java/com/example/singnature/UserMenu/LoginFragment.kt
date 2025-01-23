package com.example.singnature.UserMenu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.singnature.R
import com.example.singnature.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Initialize SharedPreferences
        sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        setupListener()

        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            // Handle login button click
            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    showToast("Username and password cannot be empty")
                    return@setOnClickListener
                }

                // Save login details to SharedPreferences
                saveLoginDetails(username, password)
                Navigation.findNavController(requireView()).navigate(R.id.userFragment)
            }

            createAccountLink.setOnClickListener {
                // Navigate to account creation screen
                root.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun saveLoginDetails(username: String, password: String) {
        with(sharedPref.edit()) {
            putString("username", username)
            putString("password", password)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }

    fun showToast(msg : String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}