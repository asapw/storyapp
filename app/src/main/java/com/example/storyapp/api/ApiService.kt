package com.example.storyapp.api

import com.example.storyapp.models.LoginRequest
import com.example.storyapp.models.LoginResponse
import com.example.storyapp.models.RegisterResponse
import com.example.storyapp.models.StoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ): StoryResponse
}