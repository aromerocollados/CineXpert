package com.arc.cinexpert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.login.LoginActivity
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
    private lateinit var noFavoritesMessage: TextView
    private lateinit var buttonEditProfile: ImageButton
    private lateinit var buttonLogout: Button
    private lateinit var userName: TextView

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
        noFavoritesMessage = view.findViewById(R.id.noFavoritesMessage)
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        userName = view.findViewById(R.id.userName)

        val layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        recyclerViewFavorites.layoutManager = layoutManager
        recyclerViewFavorites.addItemDecoration(SpaceItemDecoration(16)) // Adjust the spacing here

        favoritesAdapter = MoviesAdapter(emptyList()) { movie -> showMovieDetail(movie) }
        recyclerViewFavorites.adapter = favoritesAdapter

        buttonEditProfile.setOnClickListener {
            if (isGoogleUser()) {
                Toast.makeText(requireContext(), "Estás registrado con Google y no puedes modificar tu perfil.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                startActivity(intent)
            }
        }

        buttonLogout.setOnClickListener {
            logout()
        }

        loadUserProfile()
        listenForFavoriteMovies()
    }

    private fun isGoogleUser(): Boolean {
        val user = auth.currentUser
        user?.providerData?.forEach { profile ->
            if (profile.providerId == "google.com") {
                return true
            }
        }
        return false
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("usuarios").document(user.uid).get().addOnSuccessListener { document ->
                if (document != null) {
                    userName.text = document.getString("usuario") ?: "Usuario"
                }
            }
        }
    }

    private fun listenForFavoriteMovies() {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("favoritos")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        showNoFavoritesMessage()
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        val favoriteMovieIds = snapshots.documents.map { document ->
                            document.getLong("movieId")!!.toInt()
                        }
                        loadMoviesByIds(favoriteMovieIds)
                    } else {
                        showNoFavoritesMessage()
                    }
                }
        } else {
            showNoFavoritesMessage()
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
            updateFavoritesList(favoriteMovies)
        }
    }

    private fun updateFavoritesList(movies: List<Movie>) {
        if (movies.isEmpty()) {
            showNoFavoritesMessage()
        } else {
            hideNoFavoritesMessage()
            favoritesAdapter.updateMovies(movies)
            favoritesAdapter.notifyDataSetChanged()
        }
    }

    private fun showNoFavoritesMessage() {
        noFavoritesMessage.visibility = View.VISIBLE
        recyclerViewFavorites.visibility = View.GONE
    }

    private fun hideNoFavoritesMessage() {
        noFavoritesMessage.visibility = View.GONE
        recyclerViewFavorites.visibility = View.VISIBLE
    }

    private fun showMovieDetail(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailActivity::class.java).apply {
            putExtra("movie", movie)
        }
        startActivity(intent)
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.top = space / 2
            outRect.bottom = space / 2
        }
    }
}
