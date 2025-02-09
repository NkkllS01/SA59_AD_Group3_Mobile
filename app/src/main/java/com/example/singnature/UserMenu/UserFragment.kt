package com.example.singnature.UserMenu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.singnature.Network.ApiClient
import com.example.singnature.databinding.FragmentUserBinding
import com.example.singnature.Network.AuthService
import com.example.singnature.Network.UpdateProfileRequest
import com.example.singnature.R
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

        loadUserProfile()  // 加载当前登录用户信息
        setupListeners()   // 设置监听事件

        return binding.root
    }


    private fun loadUserProfile() {
        binding.apply {
            // 获取 SharedPreferences 中存储的用户信息
            usernameEditText.setText(sharedPref.getString("username", ""))
            emailEditText.setText(sharedPref.getString("email", ""))
            phoneEditText.setText(sharedPref.getString("phone", ""))
            subscribeWarningSwitch.isChecked = sharedPref.getBoolean("subscribeWarning", false)
            subscribeNewsletterSwitch.isChecked = sharedPref.getBoolean("subscribeNewsletter", false)
        }
    }


    private fun setupListeners() {
        binding.apply {
            // "保存修改" 按钮
            saveButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val subscribeWarning = subscribeWarningSwitch.isChecked
                val subscribeNewsletter = subscribeNewsletterSwitch.isChecked

                val updateRequest = UpdateProfileRequest(email, phone, subscribeWarning, subscribeNewsletter)
                updateUserProfile(updateRequest)
            }

            // "退出登录" 按钮
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

        // ✅ 让 Fragment 返回到 LoginFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, LoginFragment())
            .commit()
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
