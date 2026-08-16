package com.example.codebox.presentation.likes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.domain.LikeUser
import com.example.codebox.domain.TextCaseStyle
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

sealed class LikesUiState {
    object Loading : LikesUiState()
    data class Success(val users: List<LikeUser>) : LikesUiState()
    data class Error(val message: String) : LikesUiState()
}

@HiltViewModel
class LikesListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val firestore: FirebaseFirestore,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val reviewUserId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow<LikesUiState>(LikesUiState.Loading)
    val uiState: StateFlow<LikesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = LikesUiState.Loading
            try {
                val doc = firestore.collection("user_reviews")
                    .document("${reviewUserId}_${itemId}")
                    .get()
                    .await()

                if (!doc.exists()) {
                    _uiState.value = LikesUiState.Success(emptyList())
                    return@launch
                }

                val likedBy = doc.get("likedBy") as? List<String> ?: emptyList()

                if (likedBy.isEmpty()) {
                    _uiState.value = LikesUiState.Success(emptyList())
                    return@launch
                }

                val users = likedBy.mapNotNull { uid ->
                    try {
                        val userDoc = firestore.collection("users").document(uid).get().await()
                        LikeUser(
                            userId = uid,
                            nickname = userDoc.getString("nickname") ?: uid.take(6),
                            avatarUrl = userDoc.getString("avatarBase64")
                                ?: userDoc.getString("avatarUrl")
                                ?: ""
                        )
                    } catch (_: Exception) {
                        LikeUser(userId = uid, nickname = uid.take(6), avatarUrl = "")
                    }
                }

                _uiState.value = LikesUiState.Success(users)
            } catch (e: Exception) {
                _uiState.value = LikesUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}