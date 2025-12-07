package com.example.lab_week_13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _popularMovies = MutableStateFlow<List<Movie>>(emptyList())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    private fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {

            val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

            movieRepository.fetchMovies()
                .catch { e ->
                    _error.value = "An exception occurred: ${e.message}"
                }
                .collect { movies ->

                    // ✨ Assignment: Filter film tahun ini + sort by popularity (desc)
                    val filteredMovies = movies
                        .filter { it.releaseDate?.startsWith(currentYear) == true }
                        .sortedByDescending { it.popularity }

                    _popularMovies.value = filteredMovies
                }
        }
    }
}
