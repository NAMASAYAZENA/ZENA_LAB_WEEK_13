package com.example.lab_week_13

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_week_13.databinding.ActivityMainBinding
import com.example.lab_week_13.model.Movie
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val movieAdapter by lazy {
        MovieAdapter { movie -> openMovieDetails(movie) }
    }

    private fun openMovieDetails(movie: Movie) {
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
            putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
            putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
            putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 WAJIB: Set ContentView pakai DataBinding
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        // 🔥 RecyclerView lewat binding, bukan findViewById
        val recyclerView: RecyclerView = binding.movieList
        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository

        val movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            }
        )[MovieViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        movieAdapter.addMovies(movies)
                    }
                }

                launch {
                    movieViewModel.error.collect { msg ->
                        if (msg.isNotEmpty()) {
                            Snackbar.make(recyclerView, msg, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
