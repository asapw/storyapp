package com.example.storyapp.api

import android.content.Context
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.storyapp.helper.SessionManager

object Injection {

    // This method provides the StoryRepository with the necessary ApiService and authentication token
    fun provideRepository(context: Context): StoryRepository {
        // Retrieve the token from DataStore or SharedPreferences
        val token = runBlocking { SessionManager.getAuthTokenSync(context) } ?: ""

        // Ensure token is valid and passed to ApiService
        val apiService = ApiConfig.getApiService(token)

        return StoryRepository.getInstance(apiService, context)
    }
}

