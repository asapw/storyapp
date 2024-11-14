package com.example.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.models.Story
import com.bumptech.glide.Glide
import com.example.storyapp.models.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the ListStoryItem object passed from the Adapter
        val storyItem = intent.getParcelableExtra<ListStoryItem>("STORY")

        // Check if the storyItem is null and handle accordingly
        if (storyItem != null) {
            displayStoryDetails(storyItem)
        }
    }

    private fun displayStoryDetails(storyItem: ListStoryItem) {
        // Set the story name, description, and image
        binding.tvStoryName.text = storyItem.name
        binding.tvStoryDescription.text = storyItem.description

        // Use Glide to load the image URL into an ImageView
        Glide.with(this)
            .load(storyItem.photoUrl)
            .into(binding.ivStoryImage)
    }
}
