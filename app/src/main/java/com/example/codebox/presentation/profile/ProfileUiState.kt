package com.example.codebox.presentation.profile

import com.example.codebox.domain.ReviewWithItem

sealed class ProfileUiState {
    data object Loading: ProfileUiState()
    data class Success(
        val email: String,
        val nickname: String,
        val avatarUrl: String,
        val description: String,
        val reviews: List<ReviewWithItem>
    ): ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}