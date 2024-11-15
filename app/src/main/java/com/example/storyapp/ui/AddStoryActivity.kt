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
                binding.ivPhoto.setImageURI(photoUri)
            } else {
                Toast.makeText(this, "Camera capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    private val photoPickerResult = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            binding.ivPhoto.setImageURI(uri)

            Log.d("AddStoryActivity", "Selected photo URI: $photoUri")
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                openAppSettings()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyRepository = Injection.provideRepository(this)
        val viewModelFactory = StoryViewModelFactory(storyRepository)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]

        binding.buttonCamera.setOnClickListener {
            checkAndRequestCameraPermission()
        }
        binding.buttonGallery.setOnClickListener {
            openGallery()
        }

        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString().trim()

            if (photoUri != null && description.isNotEmpty()) {
                storyViewModel.addStory(photoUri!!, description)
            } else {
                Toast.makeText(this, "Please select a photo and enter a description", Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.addStorySuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Story added successfully!", Toast.LENGTH_SHORT).show()
                finish()
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
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

        photoPickerResult.launch(pickVisualMediaRequest)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

