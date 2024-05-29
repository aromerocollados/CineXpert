package com.arc.cinexpert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.movies.Movie
import com.arc.cinexpert.movies.MovieDetailActivity
import com.arc.cinexpert.movies.MoviesAdapter
import com.arc.cinexpert.movies.RetrofitInstance
import kotlinx.coroutines.launch

class SearchResultsFragment : Fragment() {
    private lateinit var searchResultsAdapter: MoviesAdapter
    private lateinit var recyclerViewSearchResults: RecyclerView
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults)
        recyclerViewSearchResults.layoutManager = GridLayoutManager(context, 2)
        searchResultsAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        recyclerViewSearchResults.adapter = searchResultsAdapter

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val query = arguments?.getString("query") ?: ""
        if (query.isNotEmpty()) {
            searchMovies(query)
        }
    }

    private fun searchMovies(query: String) {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.searchMovies(query, "f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                searchResultsAdapter.updateMovies(response.body()?.results ?: emptyList())
            }
        }
    }

    private fun showMovieDetail(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
        }
        startActivity(intent)
    }
}
