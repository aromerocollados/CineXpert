package com.arc.cinexpert.movies

data class Movie(
    val title: String,
    val poster_path: String?,
    val overview: String
)

data class MoviesResponse(
    val results: List<Movie>
)

data class CastMember(
    val name: String,
    val character: String
)

data class MovieCreditsResponse(
    val cast: List<CastMember>
)
