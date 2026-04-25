package com.movie.data.local

import com.movie.data.models.MovieDetail

fun MovieDetail.toFavoriteMovie() = FavoriteMovie(
    id = this.id ?: 0,
    title = this.title,
    overview = this.overview,
    posterPath = this.posterPath,
    backdropPath = this.backdropPath,
    voteAverage = this.voteAverage,
    releaseDate = this.releaseDate
)

fun FavoriteMovie.toMovies() = com.movie.data.models.Movies(
    id = this.id,
    title = this.title,
    overview = this.overview,
    posterPath = this.posterPath,
    backdropPath = this.backdropPath,
    voteAverage = this.voteAverage,
    releaseDate = this.releaseDate
)