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

        val storyItem = intent.getParcelableExtra<ListStoryItem>("STORY")

        if (storyItem != null) {
            displayStoryDetails(storyItem)
        }
    }

    private fun displayStoryDetails(storyItem: ListStoryItem) {
        binding.tvStoryName.text = storyItem.name
        binding.tvStoryDescription.text = storyItem.description

        Glide.with(this)
            .load(storyItem.photoUrl)
            .into(binding.ivStoryImage)
    }
}
