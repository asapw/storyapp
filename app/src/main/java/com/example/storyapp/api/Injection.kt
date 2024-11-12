package com.example.storyapp.api

import android.content.Context
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.storyapp.helper.SessionManager

object Injection {

    // This method provides the StoryRepository with the necessary ApiService
    fun provideRepository(context: Context): StoryRepository {
        // Retrieve the token from DataStore (SessionManager)
        val token = runBlocking { SessionManager.getAuthToken(context).first() } ?: ""

        // Create the ApiService with the token (ensure token is not null)
        val apiService = ApiConfig.getApiService(token)

        // Return the repository with the apiService
        return StoryRepository.getInstance(apiService)
    }
}
