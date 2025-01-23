package com.example.singnature

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.singnature.UserMenu.LoginFragment
import com.example.singnature.UserMenu.UserFragment
import com.example.singnature.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedPref : SharedPreferences
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Initialize SharedPreferences
        sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.wildlifeFragment, R.id.warningFragment, R.id.exploreFragment, R.id.userFragment)
        )
        binding.bottomNav.setupWithNavController(navController)

        initBottomNavBar()
    }

    private fun initBottomNavBar() {
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.userFragment) {
                if (!isUserLoggedIn()) {
                    navController.navigate(R.id.loginFragment)
                }
            }
        }
    }

    private fun isUserLoggedIn() : Boolean {
        return sharedPref.getBoolean("isLoggedIn", false)
    }
}