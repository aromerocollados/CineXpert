package com.arc.cinexpert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.movies.Movie
import com.arc.cinexpert.movies.MovieDetailActivity
import com.arc.cinexpert.movies.MoviesAdapter
import com.arc.cinexpert.movies.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {
    private lateinit var latestReleasesAdapter: MoviesAdapter
    private lateinit var topRatedAdapter: MoviesAdapter
    private lateinit var popularMoviesAdapter: MoviesAdapter
    private lateinit var animationMoviesAdapter: MoviesAdapter
    private lateinit var recyclerViewLatestReleases: RecyclerView
    private lateinit var recyclerViewTopRated: RecyclerView
    private lateinit var recyclerViewPopularMovies: RecyclerView
    private lateinit var recyclerViewAnimationMovies: RecyclerView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val MOVIE_DETAIL_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerViewLatestReleases = view.findViewById(R.id.recyclerViewLatestReleases)
        recyclerViewTopRated = view.findViewById(R.id.recyclerViewTopRated)
        recyclerViewPopularMovies = view.findViewById(R.id.recyclerViewPopularMovies)
        recyclerViewAnimationMovies = view.findViewById(R.id.recyclerViewAnimationMovies)

        recyclerViewLatestReleases.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTopRated.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewPopularMovies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewAnimationMovies.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        latestReleasesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        topRatedAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        popularMoviesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        animationMoviesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }

        recyclerViewLatestReleases.adapter = latestReleasesAdapter
        recyclerViewTopRated.adapter = topRatedAdapter
        recyclerViewPopularMovies.adapter = popularMoviesAdapter
        recyclerViewAnimationMovies.adapter = animationMoviesAdapter

        loadLatestReleases()
        loadTopRatedMovies()
        loadPopularMovies()
        loadAnimationMovies()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MOVIE_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle any required updates after returning from movie details
        }
    }

    private fun loadLatestReleases() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getNowPlayingMovies("f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                latestReleasesAdapter.updateMovies(response.body()?.results ?: emptyList())
            }
        }
    }

    private fun loadTopRatedMovies() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getTopRatedMovies("f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                topRatedAdapter.updateMovies(response.body()?.results ?: emptyList())
            }
        }
    }

    private fun loadPopularMovies() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getPopularMovies("f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                popularMoviesAdapter.updateMovies(response.body()?.results ?: emptyList())
            }
        }
    }

    private fun loadAnimationMovies() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getAnimationMovies("f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                animationMoviesAdapter.updateMovies(response.body()?.results ?: emptyList())
            }
        }
    }

    private fun showMovieDetail(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
        }
        startActivityForResult(intent, MOVIE_DETAIL_REQUEST_CODE)
    }
}
