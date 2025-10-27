package com.example.omdbsearch.data

import com.example.omdbsearch.network.MovieApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MovieRepository(private val api: MovieApi, private val apiKey: String) {

    suspend fun searchMovies(query: String): ResultWrapper<List<com.example.omdbsearch.model.Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val resp = api.searchMovies(apiKey = apiKey, query = query)
                // OMDb returns Response="True" or "False"
                if (resp.Response.equals("True", ignoreCase = true)) {
                    val list = resp.Search ?: emptyList()
                    ResultWrapper.Success(list)
                } else {
                    val message = "No results"
                    ResultWrapper.Failure(message)
                }
            } catch (e: IOException) {
                // network
                ResultWrapper.Failure("Network error: ${e.localizedMessage ?: "IO error"}")
            } catch (e: Exception) {
                ResultWrapper.Failure("Unexpected error: ${e.localizedMessage ?: "error"}")
            }
        }
    }
}
