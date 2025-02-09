package com.example.singnature.UserMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.singnature.Network.ApiClient
import com.example.singnature.R
import com.example.singnature.databinding.FragmentRegisterBinding
import com.example.singnature.Network.AuthService
import com.example.singnature.Network.RegisterRequest
import com.example.singnature.Network.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var authService: AuthService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        authService = ApiClient.authService

        setupListener()

        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            signUpButton.setOnClickListener {
                val username = usernameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val phone = mobileEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if (username.isEmpty() || password.isEmpty()) {
                    showToast("Username and password cannot be empty")
                    return@setOnClickListener
                }

                val request = RegisterRequest(username, password, email, phone, false, false)
                registerUser(request)
            }

            loginButton.setOnClickListener {
                root.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun registerUser(request: RegisterRequest) {
        authService.register(request).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    showToast("Registration successful")
                    binding.root.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    showToast("Registration failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
