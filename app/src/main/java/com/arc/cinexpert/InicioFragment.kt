package com.arc.cinexpert

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
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {
    private lateinit var latestReleasesAdapter: MoviesAdapter
    private lateinit var topRatedAdapter: MoviesAdapter
    private lateinit var recyclerViewLatestReleases: RecyclerView
    private lateinit var recyclerViewTopRated: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewLatestReleases = view.findViewById(R.id.recyclerViewLatestReleases)
        recyclerViewTopRated = view.findViewById(R.id.recyclerViewTopRated)

        recyclerViewLatestReleases.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTopRated.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        latestReleasesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        topRatedAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }

        recyclerViewLatestReleases.adapter = latestReleasesAdapter
        recyclerViewTopRated.adapter = topRatedAdapter

        loadLatestReleases()
        loadTopRatedMovies()
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

    private fun showMovieDetail(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java)
        intent.putExtra("movie", movie)
        startActivity(intent)
    }
}
