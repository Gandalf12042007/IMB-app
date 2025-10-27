package com.example.omdbsearch.ui

import com.example.omdbsearch.model.Movie

data class SearchUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
