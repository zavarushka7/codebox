package com.example.codebox.presentation.user_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.ReviewWithItem
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class UserProfileUiState {
    data object Loading : UserProfileUiState()
    data class Success(
        val nickname: String,
        val avatarUrl: String,
        val description: String,
        val reviews: List<ReviewWithItem>
    ) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userReviewRepository: UserReviewRepository,
    private val itemRepository: ItemRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            try {
                val doc = firestore.collection("users").document(userId).get().await()
                val nickname = doc.getString("nickname") ?: userId.take(6)
                val avatarUrl = doc.getString("avatarBase64") ?: doc.getString("avatarUrl") ?: ""
                val description = doc.getString("description") ?: ""

                val reviews = userReviewRepository.getAllReviewsForUser(userId)
                val reviewsWithItems = reviews.map { review ->
                    val item = itemRepository.getItemById(review.itemId)
                    ReviewWithItem(
                        review = review,
                        itemName = item?.name ?: review.itemId
                    )
                }

                _uiState.value = UserProfileUiState.Success(
                    nickname = nickname,
                    avatarUrl = avatarUrl,
                    description = description,
                    reviews = reviewsWithItems
                )
            } catch (e: Exception) {
                _uiState.value = UserProfileUiState.Error(e.message ?: "ошибка загрузки")
            }
        }
    }
}