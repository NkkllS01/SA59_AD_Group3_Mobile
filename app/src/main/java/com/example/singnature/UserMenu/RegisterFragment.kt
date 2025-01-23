package com.example.singnature.UserMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.singnature.R
import com.example.singnature.databinding.FragmentRegisterBinding
import kotlin.math.sign

class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        setupListener()

        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            signUpButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val email = emailEditText.text.toString()
                val mobile = mobileEditText.text.toString()
                val password = passwordEditText.text.toString()

                // To implement
                showToast("Send details to server")
                // Navigate to "To get started..."
            }

            loginButton.setOnClickListener {
                root.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
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