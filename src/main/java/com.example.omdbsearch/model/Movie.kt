package com.example.omdbsearch.model

data class Movie(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Poster: String
)

data class MovieSearchResponse(
    val Search: List<Movie>?,
    val totalResults: String?,
    val Response: String
)
