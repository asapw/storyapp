package com.example.storyapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.StoryViewModelFactory
import com.example.storyapp.api.Injection
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.viemodel.StoryViewModel

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var photoUri: Uri? = null
    private lateinit var storyViewModel: StoryViewModel

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.ivPhoto.setImageURI(photoUri) // Display captured photo
            } else {
                Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    // Using the Photo Picker API for gallery selection
    private val photoPickerResult = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            // Set the photoUri to the selected image URI from gallery
            photoUri = uri
            binding.ivPhoto.setImageURI(uri) // Display selected image

            // Log the selected URI for debugging
            Log.d("AddStoryActivity", "Selected photo URI: $photoUri")
        } else {
            // Handle case where no image was selected
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                openAppSettings() // Prompt user to enable from settings if permanently denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the StoryRepository using Injection (which will pass the token)
        val storyRepository = Injection.provideRepository(this) // Use Injection to get the repository
        val viewModelFactory = StoryViewModelFactory(storyRepository)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]

        // Handle camera and gallery button clicks
        binding.buttonCamera.setOnClickListener {
            checkAndRequestCameraPermission()
        }
        binding.buttonGallery.setOnClickListener {
            openGallery()
        }

        // Call addStory on button_add click
        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString().trim()

            // Ensure photoUri is not null before proceeding
            if (photoUri != null && description.isNotEmpty()) {
                // Proceed to add the story with the photoUri and description
                storyViewModel.addStory(photoUri!!, description)
            } else {
                // Display error message if photo is not selected or description is empty
                Toast.makeText(this, "Please select a photo and enter a description", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the addStorySuccess LiveData to handle success feedback
        storyViewModel.addStorySuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Story added successfully!", Toast.LENGTH_SHORT).show()
                finish()  // Go back to the story list screen
            } else {
                Toast.makeText(this, "Failed to add story", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "photo_${System.currentTimeMillis()}")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        photoUri?.let {
            cameraResult.launch(it)
        } ?: Toast.makeText(this, "Failed to create photo URI", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        // Ensure compatibility across different versions of Android
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

        // Launch gallery picker (SDK 33+)
        photoPickerResult.launch(pickVisualMediaRequest)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

