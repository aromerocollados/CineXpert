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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {
    private lateinit var favoritesAdapter: MoviesAdapter
    private lateinit var recyclerViewFavorites: RecyclerView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites)
        recyclerViewFavorites.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        favoritesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        recyclerViewFavorites.adapter = favoritesAdapter

        loadFavoriteMovies()
    }

    private fun loadFavoriteMovies() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("favoritos")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val favoriteMovieIds = documents.map { document ->
                        document.getLong("movieId")!!.toInt()
                    }
                    loadMoviesByIds(favoriteMovieIds)
                }
                .addOnFailureListener {
                    // Manejar el error
                }
        }
    }

    private fun loadMoviesByIds(movieIds: List<Int>) {
        lifecycleScope.launch {
            val favoriteMovies = mutableListOf<Movie>()
            movieIds.forEach { movieId ->
                val response = try {
                    RetrofitInstance.api.getMovieDetails(movieId, "f20a2909fb16470b3afbfac3fd381cba", "es-ES")
                } catch (e: Exception) { null }
                if (response?.isSuccessful == true) {
                    response.body()?.let { favoriteMovies.add(it) }
                }
            }
            favoritesAdapter.updateMovies(favoriteMovies)
        }
    }

    private fun showMovieDetail(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
        }
        startActivity(intent)
    }
}