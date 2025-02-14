package com.example.singnature.UserMenu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.singnature.Network.ApiClient
import com.example.singnature.R
import com.example.singnature.databinding.FragmentLoginBinding
import com.example.singnature.Network.AuthService
import com.example.singnature.Network.LoginRequest
import com.example.singnature.Network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private lateinit var authService: AuthService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        authService = ApiClient.authService

        setupListener()

        return binding.root
    }

    private fun setupListener() {
        binding.apply {
            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isEmpty() || password.isEmpty()) {
                    showToast("Username and password cannot be empty")
                    return@setOnClickListener
                }

                performLogin(username, password)
            }

            createAccountLink.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)

        authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    println("DEBUG: API Response -> userId=${loginResponse?.userId}, email=${loginResponse?.email}, mobile=${loginResponse?.mobile}, warning=${loginResponse?.warning}, newsletter=${loginResponse?.newsletter}")

                    loginResponse?.let {
                        println("DEBUG: API Response -> userId=${it.userId}, email=${it.email}, mobile=${it.mobile}, warning=${it.warning}, newsletter=${it.newsletter}")

                        saveLoginDetails(
                            username = username,
                            userId = it.userId,
                            email = it.email,
                            mobile = it.mobile,
                            warning = it.warning,
                            newsletter = it.newsletter
                        )
                        showToast("Login successful")
                        val shouldReturnToReportSighting = sharedPref.getBoolean("returnToReportSighting", false)
                        if (shouldReturnToReportSighting) {
                            sharedPref.edit().remove("returnToReportSighting").apply()
                            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_reportSightingFragment)
                        } else {
                            Navigation.findNavController(requireView()).navigate(R.id.userFragment)
                        }
                    }
                } else {
                    showToast("Invalid username or password")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })
    }


    private fun saveLoginDetails(username: String, userId: Int, email: String?, mobile: String?, warning: Boolean, newsletter: Boolean) {
        with(sharedPref.edit()) {
            putString("username", username)
            putInt("userId", userId)
            putString("email", email ?: "")
            putString("mobile", mobile ?: "")
            putBoolean("warning", warning)
            putBoolean("newsletter", newsletter)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }



    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
