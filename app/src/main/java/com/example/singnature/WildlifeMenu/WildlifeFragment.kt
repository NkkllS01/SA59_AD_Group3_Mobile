package com.example.singnature.WildlifeMenu

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.singnature.R
import com.example.singnature.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import kotlin.jvm.Throws

class WildlifeFragment : Fragment() {

    private lateinit var imgDisplay : ImageView
    private var currentPhotoPath : String? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_SELECT_GALLERY = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wildlife, container, false)

        val btnTakePicture = view.findViewById<Button>(R.id.btn_take_picture)
        val btnSelectGallery = view.findViewById<Button>(R.id.btn_select_gallery)
        imgDisplay = view.findViewById(R.id.img_display)

        btnTakePicture.setOnClickListener { openCamera() }
        btnSelectGallery.setOnClickListener { openGallery() }

        return view
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.let { context ->
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile : File? = try {
                    createImageFile()
                } catch (ex : IOException) {
                    null
                }

                photoFile?.also {
                    val photoURI : Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        photoFile
                    )
                    currentPhotoPath = it.absolutePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_SELECT_GALLERY)
    }

    @Throws(IOException::class)
    private fun createImageFile() : File {
        val storageDir = activity?.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_",".jpg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    imgDisplay.setImageBitmap(bitmap)

                    // Upload captured image
                    currentPhotoPath?.let { uploadImageToServer(File(it)) }
                }
                REQUEST_SELECT_GALLERY -> {
                    val selectedImage : Uri? = data?.data
                    imgDisplay.setImageURI(selectedImage)

                    // Get the file path from the URI and upload it
                    val filePath = selectedImage?.let { getPathFromUri(it) }
                    filePath?.let { uploadImageToServer(File(it)) }
                }
            }
        }
    }

    private fun uploadImageToServer(imageFile : File) {
        // Prepare the file part
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

        // Call the API
        val call = RetrofitClient.instance.uploadImage(filePart)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.toString()
                    Log.d("API Response", "Success: $responseBody")
                    Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("API Response", "Failure: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Response", "Error: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPathFromUri(uri: Uri) : String? {
        val contentResolver : ContentResolver = requireContext().contentResolver
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            val fileName = it.getString(nameIndex)

            val tempFile = File(requireContext().cacheDir, fileName)
            contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile.absolutePath
        }
    }

}