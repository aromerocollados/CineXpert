package com.arc.cinexpert.movies

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arc.cinexpert.R
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class MovieDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val movie = intent.getParcelableExtra<Movie>("movie")

        val posterImageView: ImageView = findViewById(R.id.moviePoster)
        val titleTextView: TextView = findViewById(R.id.movieTitle)
        val overviewTextView: TextView = findViewById(R.id.movieOverview)
        val actorsRecyclerView: RecyclerView = findViewById(R.id.actorsRecyclerView)

        actorsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val actorsAdapter = ActorsAdapter(emptyList())
        actorsRecyclerView.adapter = actorsAdapter

        movie?.let {
            titleTextView.text = it.title
            overviewTextView.text = it.overview
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.poster_path}")
                .into(posterImageView)

            loadActors(it)
        }
    }

    private fun loadActors(movie: Movie) {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.api.getMovieCredits(movie.id, "f20a2909fb16470b3afbfac3fd381cba")
            } catch (e: Exception) { null }
            if (response?.isSuccessful == true) {
                val actors = response.body()?.cast ?: emptyList()
                (findViewById<RecyclerView>(R.id.actorsRecyclerView).adapter as ActorsAdapter).updateActors(actors)
            }
        }
    }
}
