package com.example.storyapp.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.models.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Fetch stories from the repository
    fun fetchStories() {
        viewModelScope.launch {
            try {
                // Call the repository's method to get the stories
                val response = storyRepository.getStories()

                if (response.error == false) {
                    _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Error fetching stories"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            }
        }
    }
}
