package com.arc.cinexpert.movies

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<MoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<CreditsResponse>
}

data class CreditsResponse(
    val cast: List<Actor>
)
