package com.example.omdbsearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omdbsearch.data.MovieRepository
import com.example.omdbsearch.data.ResultWrapper
import com.example.omdbsearch.ui.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a search query") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val res = repository.searchMovies(query)) {
                is ResultWrapper.Success -> {
                    _uiState.update { it.copy(isLoading = false, movies = res.value, errorMessage = null) }
                }
                is ResultWrapper.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = res.message, movies = emptyList()) }
                }
            }
        }
    }
}
