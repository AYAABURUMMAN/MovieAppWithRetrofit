package com.movie.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.movie.data.models.Movies
import com.movie.data.remote.ApiService

class MoviePagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Movies>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movies> {
        return try {
            val currentPage = params.key ?: 1

            val response = apiService.getMovies(page = currentPage)
            val movies = response.results
            val totalPages = response.totalPages ?: 1

            LoadResult.Page(
                data = movies,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (currentPage >= totalPages) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movies>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}