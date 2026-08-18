package com.example.codebox.presentation.profile

import com.example.codebox.domain.award.AwardDisplay

sealed class AwardsUiState {
    object Loading: AwardsUiState()
    data class Loaded(val awards: List<AwardDisplay>): AwardsUiState()
    object Empty: AwardsUiState()
}