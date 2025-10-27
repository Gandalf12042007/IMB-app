package com.example.omdbsearch.network

import com.example.omdbsearch.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): MovieSearchResponse
}
