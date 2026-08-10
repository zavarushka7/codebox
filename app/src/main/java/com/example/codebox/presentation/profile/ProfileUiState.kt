package com.example.codebox.presentation.profile

sealed class ProfileUiState {
    data object Loading: ProfileUiState()
    data class Success(
        val email: String,
        val nickname: String,
        val reviews: List<ReviewWithItem>
    ): ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}