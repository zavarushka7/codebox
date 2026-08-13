package com.example.codebox.presentation.search

import com.example.codebox.domain.Item

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val items: List<com.example.codebox.domain.Item>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
