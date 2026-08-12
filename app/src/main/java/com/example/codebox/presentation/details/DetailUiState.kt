package com.example.codebox.presentation.details

import com.example.codebox.domain.Item
import com.example.codebox.domain.ReviewWithAuthor

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val item: Item, val reviews: List<ReviewWithAuthor>) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}