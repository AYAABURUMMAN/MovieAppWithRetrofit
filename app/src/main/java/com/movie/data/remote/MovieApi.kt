package com.movie.data.remote

import com.movie.data.models.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object MovieApi {

    private const val BASE_URL = "https://69cd5800ddc3cabb7bd28968.mockapi.io/movies"

    suspend fun getMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }


                    parseMovies(response)
                } else {
                    emptyList()
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun parseMovies(json: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val jsonArray = JSONArray(json)  // هاد موجود بـ Android بدون library

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            movies.add(
                Movie(
                    name = obj.optString("name", ""),
                    category = obj.optString("category", ""),
                    description = obj.optString("description", ""),
                    imageUrl = obj.optString("imageUrl", "")
                )
            )
        }
        return movies
    }
}
