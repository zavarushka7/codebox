package com.example.codebox.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.ReviewWithItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userReviewRepository: UserReviewRepository,
    private val itemRepository: ItemRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()



    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val user = Firebase.auth.currentUser
                if (user == null) {
                    _uiState.value = ProfileUiState.Error("Пользователь не авторизован")
                    return@launch
                }

                val userDoc = firestore.collection("users").document(user.uid).get().await()
                val nickname = userDoc.getString("nickname")
                    ?: user.displayName
                    ?: user.email?.substringBefore("@")
                    ?: "какой же у тебя ник, бро?"

                val reviews = userReviewRepository.getAllReviewsForUser(user.uid)
                val description = userDoc.getString("description") ?: ""
                val reviewsWithItems = reviews.map { review ->
                    val item = itemRepository.getItemById(review.itemId)
                    ReviewWithItem(
                        review = review,
                        itemName = item?.name ?: review.itemId
                    )
                }

                // внутри loadProfile, где собираешь Success:
                val avatarUrl = userDoc.getString("avatarBase64") ?: userDoc.getString("avatarUrl") ?: ""

                _uiState.value = ProfileUiState.Success(
                    email = user.email ?: "",
                    nickname = nickname,
                    avatarUrl = avatarUrl,   // теперь здесь Base64 или пусто
                    description = userDoc.getString("description") ?: "",
                    reviews = reviewsWithItems
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Ошибка загрузки профиля")
            }
        }
    }



    fun deleteReview(itemId: String){
        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser?.uid ?: return@launch
                userReviewRepository.deleteReview(userId, itemId)
                loadProfile()
            } catch (e: Exception){
                _uiState.value = ProfileUiState.Error("Не удалось удалить: ${e.message}")
            }
        }
    }
}