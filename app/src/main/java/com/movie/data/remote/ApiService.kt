package com.movie.data.remote

import com.movie.data.models.ApiListResponse
import com.movie.data.models.MovieDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun getMovies(
        @Query("page") page: Int = 1
    ): ApiListResponse

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int
    ): MovieDetail
}