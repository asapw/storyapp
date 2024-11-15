package com.example.storyapp.api

import android.content.Context
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.runBlocking
import com.example.storyapp.helper.SessionManager

object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val token = runBlocking { SessionManager.getAuthTokenSync(context) } ?: ""

        val apiService = ApiConfig.getApiService(token)

        return StoryRepository.getInstance(apiService, context)
    }
}

