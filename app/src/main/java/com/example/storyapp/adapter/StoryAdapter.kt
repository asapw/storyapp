package com.example.storyapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.models.ListStoryItem
import com.example.storyapp.models.Story
import com.example.storyapp.ui.DetailStoryActivity

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyItem = getItem(position) // this is ListStoryItem

        holder.binding.tvStoryName.text = storyItem.name
        holder.binding.tvStoryDescription.text = storyItem.description

        // Use Glide to load the image
        Glide.with(holder.itemView.context)
            .load(storyItem.photoUrl)
            .into(holder.binding.ivStoryImage)

        // Set item click listener
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailStoryActivity::class.java)
            intent.putExtra("STORY", storyItem)  // Pass the ListStoryItem object
            context.startActivity(intent)
        }
    }

    class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)
}

class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}