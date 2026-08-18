package com.example.codebox.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.Item
import com.example.codebox.domain.review.UserReview
import com.example.codebox.domain.text_style.TextCaseStyle
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val userReviewRepository: UserReviewRepository,
    settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val userId: String = Firebase.auth.currentUser?.uid ?: ""

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _review = MutableStateFlow(UserReview(userId = userId, itemId = itemId))
    val review: StateFlow<UserReview> = _review.asStateFlow()

    fun load(itemId: String) {
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val item = itemRepository.getItemById(itemId)
                if (item == null) {
                    _uiState.value = DetailUiState.Error("айтем не найден")
                    return@launch
                }
                val reviews = userReviewRepository.getReviewsForItem(itemId)
                val myReview = userReviewRepository.getReview(userId, itemId)
                _uiState.value = DetailUiState.Success(item, reviews, myReview)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "ошибка загрузки")
            }
        }
    }

    fun onCommentChange(new: String) {
        _review.value = _review.value.copy(comment = new)
    }

    fun onRatingChange(new: Int) {
        _review.value = _review.value.copy(rating = new)
    }

    fun saveReview() {
        viewModelScope.launch {
            userReviewRepository.saveReview(_review.value)
        }
    }
}