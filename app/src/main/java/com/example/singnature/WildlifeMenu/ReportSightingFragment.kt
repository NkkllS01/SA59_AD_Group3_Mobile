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
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
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
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.Date

class ReportSightingFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentReportSightingBinding? = null
    private val binding get() = _binding!!

    private var userLocation: LatLng? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private var imageUri: Uri? = null
    private var photoFile: File? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val CAPTURE_IMAGE_REQUEST = 2
        private const val STORAGE_PERMISSION_CODE = 1001
    }

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
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Acquire user input
        val speciesName = binding.sightingTypeSpinner.selectedItem.toString()

        if (speciesName == "Select Species Name"){
            Toast.makeText(requireContext(), "Please select species name", Toast.LENGTH_SHORT).show()
            return
        }
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
        val file = imageUri?.let { copyFileToCache(it) }
        if(file == null){
            Toast.makeText(requireContext(), "Image file not found", Toast.LENGTH_SHORT).show()
            return
        }
        val gson = Gson()
        val sightingJson = gson.toJson(Sightings(
            sightingId = 0,
            userId = sharedPreferences.getInt("userId",0),
            userName = sharedPreferences.getString("username", "").toString(),
            date = date,
            specieId = speciesID,
            specieName = speciesName,
            details = details,
            imageUrl = "",
            latitude = latitude,
            longitude = longitude,
        ))

        Log.d("SightingData", sightingJson)

        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(),sightingJson)
        val fileRequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(),file)
        val filePart = MultipartBody.Part.createFormData("file",file.name,fileRequestBody)

        sightingsApiService.createSighting(requestBody,filePart).enqueue(object :retrofit2.Callback<Sightings>{
            override fun onResponse(call: Call<Sightings>, response: Response<Sightings>) {
                 if(response.isSuccessful){
                     AlertDialog.Builder(requireContext())
                         .setTitle("Submission Successful")
                         .setMessage("Sighting report submitted successfully!")
                         .setPositiveButton("OK") { dialog, which ->
                             findNavController().navigate(R.id.action_reportSightingFragment_to_wildlifeMapsFragment)
                         }
                         .setCancelable(false)
                         .create()
                         .show()
                 }else{
                   val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                   Log.e("CreateSighting", "Error: $errorMessage")
                   Toast.makeText(requireContext(),"Error: $errorMessage",Toast.LENGTH_SHORT).show()
                }
             }

          override fun onFailure(call: Call<Sightings>, t: Throwable) {
              Toast.makeText(requireContext(),"Sighting submission failed: ${t.message} ",Toast.LENGTH_SHORT).show()
              Log.e("Submission falied","ERROR: ${t.message}")
          }
      })
    }

    private fun copyFileToCache(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
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
        val permissions = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(requireActivity(), permissions.toTypedArray(), 1001)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}