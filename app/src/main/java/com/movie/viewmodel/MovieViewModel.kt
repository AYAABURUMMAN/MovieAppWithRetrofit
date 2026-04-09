package com.movie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie.data.models.MovieDetail
import com.movie.data.models.Movies
import com.movie.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movieList = MutableStateFlow<List<Movies>>(emptyList())
    val movieList: StateFlow<List<Movies>> = _movieList

    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail: StateFlow<MovieDetail?> = _movieDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiService = ApiClient.create()

    init { fetchMovies() }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                _movieList.value = apiService.getMovies().results
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error: ${e.message}")
            }
        }
    }

    fun fetchMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movieDetail.value = apiService.getMovieDetail(movieId)
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}