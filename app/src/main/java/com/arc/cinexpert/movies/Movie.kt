package com.arc.cinexpert.movies

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val vote_average: Double
) : Parcelable


data class MoviesResponse(
    val results: List<Movie>
)

@Parcelize
data class Actor(
    val name: String,
    val profile_path: String?
) : Parcelable