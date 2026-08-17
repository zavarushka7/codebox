package com.example.codebox.presentation.profile

import com.example.codebox.domain.ReviewWithItem

import com.example.codebox.domain.AwardDisplay

sealed class ProfileUiState {
    object Loading: ProfileUiState()
    data class Success(
        val email: String?,
        val nickname: String,
        val avatarUrl: String,
        val description: String,
        val reviews: List<ReviewWithItem> = emptyList(),
        val awards: List<AwardDisplay> = emptyList()
    ): ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}