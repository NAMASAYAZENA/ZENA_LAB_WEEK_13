package com.example.lab_week_13

import android.util.Log
import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDatabase
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {

    private val apiKey = BuildConfig.TMDB_API_KEY

    // Flow utama (UI)
    fun fetchMovies(): Flow<List<Movie>> = flow {

        // 1. Load from local Room
        val localMovies = movieDatabase.movieDao().getMovies()
        if (localMovies.isNotEmpty()) {
            emit(localMovies)
        }

        // 2. Fetch API
        val response = movieService.getPopularMovies(apiKey)

        // 3. Save to Room
        movieDatabase.movieDao().addMovies(response.results)

        // 4. Emit data baru
        emit(response.results)

    }.flowOn(Dispatchers.IO)


    // modul part 3
    suspend fun fetchMoviesFromNetwork() {
        try {
            val response = movieService.getPopularMovies(apiKey)
            movieDatabase.movieDao().addMovies(response.results)

            Log.d("MovieRepository", "Worker updated ${response.results.size} movies")
        } catch (e: Exception) {
            Log.e("MovieRepository", "Worker error: ${e.message}")
        }
    }
}
