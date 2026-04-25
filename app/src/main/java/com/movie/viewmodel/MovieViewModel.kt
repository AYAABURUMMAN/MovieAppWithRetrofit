package com.movie.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.movie.data.local.MovieDatabase
import com.movie.data.local.toFavoriteMovie
import com.movie.data.local.toMovies
import com.movie.data.models.MovieDetail
import com.movie.data.models.Movies
import com.movie.data.paging.MoviePagingSource
import com.movie.data.remote.ApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.create()
    private val dao = MovieDatabase.getInstance(application).favoriteMovieDao()

    val favorites: StateFlow<List<Movies>> = dao.getAllFavorites()
        .map { list -> list.map { it.toMovies() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentMovieId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val isFavorite: StateFlow<Boolean> = _currentMovieId
        .flatMapLatest { movieId ->
            if (movieId == null) flowOf(false)
            else dao.isFavorite(movieId).map { count -> count > 0 }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Paging
    val moviePager = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { MoviePagingSource(apiService) }
    ).flow.cachedIn(viewModelScope)

    // Detail
    private val _movieDetail = MutableStateFlow<MovieDetail?>(null)
    val movieDetail: StateFlow<MovieDetail?> = _movieDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchMovieDetail(movieId: Int) {
        _currentMovieId.value = movieId

        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movieDetail.value = apiService.getMovieDetail(movieId)
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching detail: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(movie: MovieDetail) {
        viewModelScope.launch {
            val currentlyFavorite = _currentMovieId.value?.let { id ->
                dao.isFavorite(id).map { it > 0 }
            }
            if (isFavorite.value) {
                dao.delete(movie.toFavoriteMovie())
            } else {
                dao.insert(movie.toFavoriteMovie())
            }
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.AndroidViewModelFactory(application) {}
        }
    }
}