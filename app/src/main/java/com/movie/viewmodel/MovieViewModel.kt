package com.movie.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie.data.models.Movie
import com.movie.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _movieList = MutableStateFlow<List<Movie>>(emptyList())
    val movieList: StateFlow<List<Movie>> = _movieList
    private val apiService = ApiClient.create()
    init {
        fetchMovies()
    }
    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                _movieList.value = apiService.getMovies()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error: ${e.message}")
            }
        }
    }
}