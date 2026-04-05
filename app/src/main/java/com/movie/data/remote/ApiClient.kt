package com.movie.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object ApiClient {
    fun create(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://69cd5800ddc3cabb7bd28968.mockapi.io/") // Replace with your API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}