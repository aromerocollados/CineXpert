package com.arc.cinexpert.movies

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/upcoming")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<MoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<MoviesResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<CreditsResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<Movie>

    @GET("discover/movie")
    suspend fun getAnimationMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("with_genres") genre: String = "16"
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<WatchProvidersResponse>
}

data class CreditsResponse(
    val cast: List<Actor>
)
