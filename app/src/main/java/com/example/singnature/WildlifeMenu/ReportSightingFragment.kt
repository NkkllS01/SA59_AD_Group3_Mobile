package com.example.singnature.WildlifeMenu

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.singnature.Network.sightingsApiService
import com.example.singnature.R
import com.example.singnature.databinding.FragmentReportSightingBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Response
import java.util.Date

class ReportSightingFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentReportSightingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private val PICK_IMAGE_REQUEST = 1
    private val CAPTURE_IMAGE_REQUEST = 2
    private var userLocation: LatLng? = null
    private var imageUri: Uri? = null
    private lateinit var sighting:Sightings

    private val speciesList = arrayOf(
        "Select Species Name",
        "Asian Hornets",
        "Basket Stinkhorn",
        "Bracket Fungi",
        "Cloud Monitor Lizards",
        "Digger Bee",
        "Dumerils Monitor Lizards",
        "Giant Honey Bees",
        "Collared Earthstar",
        "Maylaysian Water Monitor Lizards"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportSightingBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sightingImageView.setImageResource(R.drawable.image_placeholder)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            sharedPreferences.edit().putBoolean("returnToReportSighting", true).apply()
            findNavController().navigate(R.id.action_reportSightingFragment_to_loginFragment)
        } else {
            val userName = sharedPreferences.getString("username", "").toString()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, speciesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sightingTypeSpinner.adapter = adapter

        requestPermissions()

        binding.buttonUpload.setOnClickListener {
            showImageSourceDialog()
        }

        binding.buttonSubmit.setOnClickListener {
            handleSubmit()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun handleSubmit() {
        // Acquire user input
        val speciesName = binding.sightingTypeSpinner.selectedItem.toString()
        val details = binding.sightingDetails.text.toString()
        val date = Date()
        val latitude = selectedLocation?.latitude ?: userLocation?.latitude ?: 1.291896
        val longitude = selectedLocation?.longitude ?: userLocation?.longitude ?: 103.776642
        val speciesID: Int = when (speciesName) {
            "Giant Honey Bees" -> 1
            "Digger Bee" -> 2
            "Asian Hornets" -> 3
            "Cloud Monitor Lizards" -> 4
            "Maylayan Water Monitor Lizards" -> 5
            "Dumerils Monitor Lizards" -> 6
            "Basket Stinkhorn" -> 7
            "Bracket Fungi" -> 8
            "Collared Earthstar" -> 9
            else -> -1
        }
        sighting = Sightings(
            sightingId = 0, // Suppose ID is generated by backend
            userId = sharedPreferences.getInt("userId",0),
            userName = sharedPreferences.getString("username", "").toString(),
            date = date,
            specieId = speciesID,
            specieName = speciesName,
            details = details,
            imageUrl = "", // Need upload-to-cloud implementations
            latitude = latitude,
            longitude = longitude,
//            status = Sightings.SightingStatus.Active
        )
        Log.e("sighting object confirmation","$sighting")
        //SQL logics, TO BE IMPLEMENTED
        sightingsApiService.createSighting(sighting).enqueue(object :retrofit2.Callback<Sightings>{
            override fun onResponse(call: Call<Sightings>, response: Response<Sightings>) {
                if(response.isSuccessful){
                    Toast.makeText(requireContext(),"Sighting submitted successfully!",Toast.LENGTH_SHORT).show()
                }else{
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("CreateSighting", "Error: $errorMessage")
                    Toast.makeText(requireContext(),"Error: $errorMessage",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Sightings>, t: Throwable) {
                Toast.makeText(requireContext(),"Sighting submission failed: ${t.message} ",Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showImageSourceDialog() {
        val options = arrayOf("Choose from Gallery", "Take Photo", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (options[which]) {
                "Choose from Gallery" -> openGallery()
                "Take Photo" -> openCamera()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data?.data
                    binding.sightingImageView.setImageURI(imageUri)
                }
                CAPTURE_IMAGE_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.sightingImageView.setImageBitmap(bitmap)
                }
            }
        } else {
            Toast.makeText(context, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))
                    googleMap.addMarker(MarkerOptions().position(userLocation!!).title("Your Location"))

                    googleMap.setOnMapClickListener { latLng ->
                        selectedLocation = latLng
                        googleMap.clear()
                        googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),500,null)
                    }
                } else {
                    Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAPTURE_IMAGE_REQUEST)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}