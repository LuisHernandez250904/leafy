package com.example.leafy.api

import com.example.leafy.model.PlantNetResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PlantNetService {

    @Multipart
    @POST("identify/all")
    suspend fun identifyPlant(
        @Part images: List<MultipartBody.Part>,
        @Query("api-key") apiKey: String
    ): PlantNetResponse
}