package com.arc.cinexpert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.movies.MoviesAdapter
import com.arc.cinexpert.movies.RetrofitInstance
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var moviesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesRecyclerView = view.findViewById(R.id.recyclerViewEstrenos)
        moviesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        moviesAdapter = MoviesAdapter(emptyList())
        moviesRecyclerView.adapter = moviesAdapter

        loadMovies()
    }

    private fun loadMovies() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getNowPlayingMovies("f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) {
                null  // Manejar la excepción de red
            }
            if (response?.isSuccessful == true) {
                val movies = response.body()?.results ?: emptyList()
                moviesAdapter.updateMovies(movies)
            } else {
                // Log or handle API response error
            }
        }
    }
}
