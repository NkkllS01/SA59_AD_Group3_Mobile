package com.example.singnature.UserMenu

import android.content.Context
import android.content.SharedPreferences
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
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        sharedPref = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

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
                    showToast("username and password can not be empty")
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
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    showToast("register successful")

                    // 保存用户登录信息
                    saveLoginDetails(
                        username = request.username,
                        userId = user.userId,
                        email = user.email,
                        mobile = user.mobile,
                        warning = user.warning,
                        newsletter = user.newsletter
                    )

                    // 跳转到 UserFragment
                    binding.root.findNavController().navigate(R.id.action_registerFragment_to_userFragment)
                } else {
                    showToast("register filed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("network error: ${t.message}")
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
