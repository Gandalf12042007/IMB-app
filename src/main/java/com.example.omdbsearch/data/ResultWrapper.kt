package com.example.omdbsearch.data

sealed class ResultWrapper<out T> {
    data class Success<T>(val value: T) : ResultWrapper<T>()
    data class Failure(val message: String) : ResultWrapper<Nothing>()
}
