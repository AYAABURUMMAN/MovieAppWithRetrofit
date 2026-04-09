package com.movie.data.remote

import com.movie.data.models.ApiListResponse
import com.movie.data.models.MovieDetail
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("movie/popular")
    suspend fun getMovies(): ApiListResponse
    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int
    ): MovieDetail
}
