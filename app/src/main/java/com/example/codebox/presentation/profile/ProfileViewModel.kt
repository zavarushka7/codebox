package com.example.codebox.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.ReviewWithItem
import com.example.codebox.domain.TextCaseStyle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userReviewRepository: UserReviewRepository,
    private val itemRepository: ItemRepository,
    private val firestore: FirebaseFirestore,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private val profileUserId: String = savedStateHandle.get<String>("userId")
        ?: Firebase.auth.currentUser?.uid
        ?: ""
    val isReadOnly: Boolean = profileUserId != Firebase.auth.currentUser?.uid

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val userDoc = firestore.collection("users").document(profileUserId).get().await()

                val nickname = userDoc.getString("nickname")
                    ?: userDoc.getString("displayName")
                    ?: "пользователь"

                val reviews = userReviewRepository.getAllReviewsForUser(profileUserId)
                val description = userDoc.getString("description") ?: ""

                val reviewsWithItems = reviews.map { review ->
                    val item = itemRepository.getItemById(review.itemId)
                    ReviewWithItem(
                        review = review,
                        itemName = item?.name ?: review.itemId
                    )
                }

                val avatarUrl = userDoc.getString("avatarBase64")
                    ?: userDoc.getString("avatarUrl")
                    ?: ""

                // Email берём из документа, т.к. у чужого профиля нет FirebaseUser
                val email = userDoc.getString("email") ?: ""

                _uiState.value = ProfileUiState.Success(
                    email = email,
                    nickname = nickname,
                    avatarUrl = avatarUrl,
                    description = description,
                    reviews = reviewsWithItems
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Ошибка загрузки профиля")
            }
        }
    }

    fun deleteReview(itemId: String) {
        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser?.uid ?: return@launch
                userReviewRepository.deleteReview(userId, itemId)
                loadProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Не удалось удалить: ${e.message}")
            }
        }
    }
}