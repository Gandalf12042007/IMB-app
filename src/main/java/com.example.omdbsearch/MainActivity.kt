package com.example.omdbsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.example.omdbsearch.data.MovieRepository
import com.example.omdbsearch.network.NetworkModule
import com.example.omdbsearch.ui.SearchUiState
import com.example.omdbsearch.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {

    // IMPORTANT: Replace with your actual key
    private val OMDB_API_KEY = "YOUR_OMDB_API_KEY"

    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val api = NetworkModule.createMovieApi()
            val repo = MovieRepository(api, OMDB_API_KEY)
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repo) as T
        }
    }

    private val vm: MovieViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val uiState by vm.uiState.collectAsState()

                    AppContent(uiState = uiState,
                        onQueryChange = { vm.updateQuery(it) },
                        onSearch = { vm.search() },
                        onRetry = { vm.search() },
                        onClearError = { vm.clearError() })
                }
            }
        }
    }
}

@Composable
fun AppContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            query = uiState.query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                CircularProgressIndicator()
            }
        } else {
            if (uiState.errorMessage != null) {
                ErrorView(message = uiState.errorMessage, onRetry = onRetry, onDismiss = onClearError)
            } else {
                MovieList(movies = uiState.movies)
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            label = { Text("Search movies (e.g. Batman)") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSearch, modifier = Modifier.size(56.dp)) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, onDismiss: () -> Unit) {
    Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Error", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Text(message)
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Button(onClick = onRetry) { Text("Retry") }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onDismiss) { Text("Dismiss") }
            }
        }
    }
}

@Composable
fun MovieList(movies: List<com.example.omdbsearch.model.Movie>) {
    if (movies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text("No movies yet. Try searching!", modifier = Modifier.padding(top = 20.dp))
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            MovieRow(movie = movie)
            Divider()
        }
    }
}

@Composable
fun MovieRow(movie: com.example.omdbsearch.model.Movie) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
        val painter = rememberAsyncImagePainter(model = movie.Poster)
        Image(
            painter = painter,
            contentDescription = movie.Title,
            modifier = Modifier
                .size(92.dp)
                .padding(end = 12.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(movie.Title, style = MaterialTheme.typography.h6, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Year: ${movie.Year}", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(6.dp))
            Text("imdbID: ${movie.imdbID}", style = MaterialTheme.typography.caption)
        }
    }
}
