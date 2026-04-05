package com.movie.data.remote

import com.movie.data.models.Movie
import retrofit2.http.GET

interface ApiService {
    @GET("movies")
    suspend fun getMovies(): List<Movie>
}