package com.arc.cinexpert.movies

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var addToFavoritesButton: ImageButton
    private lateinit var providersTextView: TextView
    private lateinit var movieRating: TextView
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val movie = intent.getParcelableExtra<Movie>("movie")

        val posterImageView: ImageView = findViewById(R.id.moviePoster)
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val actorsRecyclerView: RecyclerView = findViewById(R.id.actorsRecyclerView)
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton)
        providersTextView = findViewById(R.id.providersTextView)
        movieRating = findViewById(R.id.movieRating)

        actorsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val actorsAdapter = ActorsAdapter(emptyList())
        actorsRecyclerView.adapter = actorsAdapter

        movie?.let { movie ->
            titleTextView.text = movie.title
            overviewTextView.text = movie.overview
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(posterImageView)

            // Asignar el rating de la película
            movieRating.text = movie.vote_average.toString()

            checkIfFavorite(movie.id)
            loadActors(movie.id)
            loadWatchProviders(movie.id)

            addToFavoritesButton.setOnClickListener {
                if (isFavorite) {
                    removeFromFavorites(movie)
                } else {
                    addToFavorites(movie)
                }
            }
        }
    }

    private fun loadActors(movieId: Int) {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getMovieCredits(movieId, "f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                val actors = response.body()?.cast ?: emptyList()
                (findViewById<RecyclerView>(R.id.actorsRecyclerView).adapter as ActorsAdapter).updateActors(actors)
            }
        }
    }

    private fun loadWatchProviders(movieId: Int) {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getMovieWatchProviders(movieId, "f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                val providers = response.body()?.results?.get("ES")?.flatrate ?: emptyList()
                val providerNames = providers.joinToString(", ") { it.provider_name }
                providersTextView.text = if (providerNames.isNotEmpty()) "Disponible en: $providerNames" else "No disponible en plataformas de streaming"
            }
        }
    }

    private fun addToFavorites(movie: Movie) {
        val user = auth.currentUser
        if (user != null) {
            val favoriteMovie = hashMapOf(
                "userId" to user.uid,
                "movieId" to movie.id
            )

            firestore.collection("favoritos")
                .add(favoriteMovie)
                .addOnSuccessListener {
                    Toast.makeText(this, "Añadido a Favoritos", Toast.LENGTH_SHORT).show()
                    isFavorite = true
                    addToFavoritesButton.setImageResource(R.drawable.ic_star_filled)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al añadir a Favoritos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Inicia sesión para añadir a Favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromFavorites(movie: Movie) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("favoritos")
                .whereEqualTo("userId", user.uid)
                .whereEqualTo("movieId", movie.id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        firestore.collection("favoritos").document(document.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show()
                                isFavorite = false
                                addToFavoritesButton.setImageResource(R.drawable.ic_star_border)
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar de Favoritos", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al buscar en Favoritos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Inicia sesión para eliminar de Favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfFavorite(movieId: Int) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("favoritos")
                .whereEqualTo("userId", user.uid)
                .whereEqualTo("movieId", movieId)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        isFavorite = false
                        addToFavoritesButton.setImageResource(R.drawable.ic_star_border)
                    } else {
                        isFavorite = true
                        addToFavoritesButton.setImageResource(R.drawable.ic_star_filled)
                    }
                }
        }
    }
}
