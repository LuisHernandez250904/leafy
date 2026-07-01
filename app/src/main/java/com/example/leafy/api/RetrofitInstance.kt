package com.example.leafy.api

import com.example.leafy.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // 🔹 Esto mostrará TODO el request/response en Logcat (okhttp)
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val api: PlantNetService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.PLANT_NET_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlantNetService::class.java)
    }
}