package com.example.codebox.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.Item
import com.example.codebox.domain.UserReview
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val userReviewRepository: UserReviewRepository,
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val userId: String = Firebase.auth.currentUser?.uid ?: ""

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _review = MutableStateFlow(UserReview(userId = userId, itemId = itemId))
    val review: StateFlow<UserReview> = _review.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        viewModelScope.launch {
            _item.value = itemRepository.getItemById(itemId)
            userReviewRepository.getReview(userId, itemId)?.let {
                _review.value = it
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
            _saved.value = true
        }
    }
}