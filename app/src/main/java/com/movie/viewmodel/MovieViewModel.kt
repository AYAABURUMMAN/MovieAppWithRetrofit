package com.movie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.movie.data.models.MovieDetail
import com.movie.data.paging.MoviePagingSource
import com.movie.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.create()


    // Paging
    val moviePager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { MoviePagingSource(apiService) }
    ).flow.cachedIn(viewModelScope)

    // Detail
    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail: StateFlow<MovieDetail?> = _movieDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading




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