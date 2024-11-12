package com.example.storyapp.repository

import com.example.storyapp.api.ApiService
import com.example.storyapp.models.StoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class StoryRepository(private val apiService: ApiService) {

    // Fetch stories from the API
    suspend fun getStories(): StoryResponse {
        return withContext(Dispatchers.IO) {
            apiService.getStories()
        }
    }

    companion object {
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository {
            if (instance == null) {
                instance = StoryRepository(apiService)
            }
            return instance!!
        }
    }
}
