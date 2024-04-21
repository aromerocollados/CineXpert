package com.arc.cinexpert.movies

data class Movie(
    val title: String,
    val poster_path: String?,
    val overview: String
)

data class MoviesResponse(
    val results: List<Movie>
)