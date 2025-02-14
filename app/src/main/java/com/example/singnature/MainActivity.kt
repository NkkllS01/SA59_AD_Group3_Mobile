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
import androidx.navigation.ui.setupActionBarWithNavController
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

        sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initNavController()

        initBottomNavBar()
    }

    private fun initNavController() {
        // Initialize NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        // Set top-level destinations for AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.wildlifeMapsFragment,
                R.id.warningFragment,
                R.id.exploreFragment,
                R.id.userFragment)
        )

        binding.bottomNav.setupWithNavController(navController)
    }

    /*
    private fun initBottomNavBar() {
        // Check if user is logged in for UserFragment
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.userFragment -> {
                    // Check if user is logged in
                    if (isUserLoggedIn()) {
                        navController.navigate(R.id.userFragment)
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    true
                }
            }
        }
    }
    */

    private fun initBottomNavBar() {
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.wildlifeMapsFragment -> {
                    if (navController.currentDestination?.id != R.id.wildlifeMapsFragment) {
                        navController.navigate(R.id.wildlifeMapsFragment)
                    }
                    true
                }
                R.id.userFragment -> {
                    if (isUserLoggedIn()) {
                        navController.navigate(R.id.userFragment)
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    true
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.userFragment && !isUserLoggedIn()) {
                navController.navigate(R.id.loginFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun isUserLoggedIn() : Boolean {
        return sharedPref.getBoolean("isLoggedIn", false)
    }
}
