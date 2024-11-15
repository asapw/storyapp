package com.example.storyapp.viemodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.models.ListStoryItem
import kotlinx.coroutines.launch
import android.net.Uri


class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _addStorySuccess = MutableLiveData<Boolean>()
    val addStorySuccess: LiveData<Boolean> = _addStorySuccess

    fun fetchStories() {
        viewModelScope.launch {
            try {
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

    fun addStory(photoUri: Uri, description: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.addNewStory(photoUri, description)
                if (response.error == false) {
                    _addStorySuccess.value = true
                    fetchStories()
                } else {
                    _errorMessage.value = response.message ?: "Failed to add story"
                    _addStorySuccess.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
                _addStorySuccess.value = false
            }
        }
    }
}
