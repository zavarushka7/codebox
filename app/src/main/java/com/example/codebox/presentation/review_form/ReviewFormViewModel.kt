package com.example.codebox.presentation.review_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.Item
import com.example.codebox.domain.UserReview
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
class ReviewFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val userReviewRepository: UserReviewRepository,
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val reviewUserId: String = savedStateHandle.get<String>("userId")
        ?: Firebase.auth.currentUser?.uid
        ?: ""

    val isReadOnly: Boolean = reviewUserId != Firebase.auth.currentUser?.uid

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _review = MutableStateFlow(UserReview(userId = reviewUserId, itemId = itemId))
    val review: StateFlow<UserReview> = _review.asStateFlow()

    private val _authorName = MutableStateFlow("")
    val authorName: StateFlow<String> = _authorName.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _item.value = itemRepository.getItemById(itemId)
            userReviewRepository.getReview(reviewUserId, itemId)?.let {
                _review.value = it
            }
            if (isReadOnly && reviewUserId.isNotBlank()) {
                try {
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users").document(reviewUserId).get().await()
                    _authorName.value = doc.getString("nickname") ?: reviewUserId.take(6)
                } catch (_: Exception) {
                    _authorName.value = reviewUserId.take(6)
                }
            }
        }
    }

    fun onCommentChange(new: String) {
        if (isReadOnly) return
        _review.value = _review.value.copy(comment = new)
    }

    fun onRatingChange(new: Int) {
        if (isReadOnly) return
        _review.value = _review.value.copy(rating = new)
    }

    fun saveReview() {
        if (isReadOnly) return
        viewModelScope.launch {
            userReviewRepository.saveReview(_review.value)
        }
    }
}