package com.movie.data.remote

import io.nerdythings.okhttp.profiler.BuildConfig
import io.nerdythings.okhttp.profiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object ApiClient {
    private const val BEARER_TOKEN ="eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NTQ1OTVkZGM1ZjAxYWQ5MDM1YTc4MDExYmMxZDI4ZSIsIm5iZiI6MTY2NjA4NjYwNy41MDMsInN1YiI6IjYzNGU3NmNmYzE3NWIyMDA4MmRlNTBiZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Q4ZETXDPP7OF1GMyWhCzA0gjne-aH7P7p7FW-rKFRA0"
    fun create(): ApiService {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $BEARER_TOKEN")
                .build()
            chain.proceed(request)
        }
        if (BuildConfig.DEBUG) { builder.addInterceptor(
            OkHttpProfilerInterceptor()
        ) }

        val client = builder.build()
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
        .client(client)
            .build()
            .create(ApiService::class.java)
    }
}