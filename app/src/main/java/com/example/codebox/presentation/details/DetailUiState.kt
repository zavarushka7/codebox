package com.example.codebox.presentation.details

import com.example.codebox.domain.Item
import com.example.codebox.domain.ReviewWithAuthor
import com.example.codebox.domain.UserReview

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(
        val item: Item,
        val reviews: List<ReviewWithAuthor>,
        val myReview: UserReview?
    ) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}