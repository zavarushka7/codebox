package com.example.codebox.presentation.review_form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.Item
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.domain.review.UserReview
import com.example.codebox.domain.service.NotificationService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReviewFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val userReviewRepository: UserReviewRepository,
    settingsRepository: SettingsRepository,
    private val notificationService: NotificationService
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val reviewUserId: String = savedStateHandle.get<String>("userId")
        ?: Firebase.auth.currentUser?.uid
        ?: ""
    private val currentUserId: String = Firebase.auth.currentUser?.uid ?: ""

    val isReadOnly: Boolean = reviewUserId != currentUserId

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _review = MutableStateFlow(UserReview(userId = reviewUserId, itemId = itemId))
    val review: StateFlow<UserReview> = _review.asStateFlow()

    private val _authorName = MutableStateFlow("")
    val authorName: StateFlow<String> = _authorName.asStateFlow()

    private val _authorAvatarUrl = MutableStateFlow("")
    val authorAvatarUrl: StateFlow<String> = _authorAvatarUrl.asStateFlow()

    private val _likeCount = MutableStateFlow(0)
    val likeCount: StateFlow<Int> = _likeCount.asStateFlow()

    private val _isLikedByMe = MutableStateFlow(false)
    val isLikedByMe: StateFlow<Boolean> = _isLikedByMe.asStateFlow()

    private val _saveComplete = MutableSharedFlow<Unit>()
    val saveComplete: SharedFlow<Unit> = _saveComplete.asSharedFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _item.value = itemRepository.getItemById(itemId)
            userReviewRepository.getReview(reviewUserId, itemId)?.let {
                _review.value = it
                _likeCount.value = it.countLikes
                _isLikedByMe.value = it.likedBy.contains(currentUserId)
            }

            if (isReadOnly && reviewUserId.isNotBlank()) {
                try {
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users").document(reviewUserId).get().await()
                    _authorName.value = doc.getString("nickname") ?: reviewUserId.take(6)
                    _authorAvatarUrl.value = doc.getString("avatarBase64")
                        ?: doc.getString("avatarUrl")
                                ?: ""
                } catch (_: Exception) {
                    _authorName.value = reviewUserId.take(6)
                    _authorAvatarUrl.value = ""
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
        if (_isSaving.value) return

        _isSaving.value = true
        val review = _review.value

        viewModelScope.launch {
            try {
                userReviewRepository.saveReview(review)
                notificationService.notifyReviewCreated(
                    userId = currentUserId,
                    itemName = _item.value?.name ?: "",
                    itemId = review.itemId,
                    rating = review.rating,
                    comment = review.comment
                )
                _isSaving.value = false
                _saveComplete.emit(Unit)
            } catch (e: Exception) {
                _isSaving.value = false
            }
        }
    }

    fun toggleLike() {
        viewModelScope.launch {
            val currentlyLiked = _isLikedByMe.value
            val newCount = if (currentlyLiked) _likeCount.value - 1 else _likeCount.value + 1
            val newLikedBy = if (currentlyLiked) {
                _review.value.likedBy - currentUserId
            } else {
                _review.value.likedBy + currentUserId
            }

            _likeCount.value = newCount
            _isLikedByMe.value = !currentlyLiked
            _review.value = _review.value.copy(
                countLikes = newCount,
                likedBy = newLikedBy
            )

            userReviewRepository.saveReview(_review.value)
            if (!currentlyLiked && reviewUserId != currentUserId) {
                notificationService.notifySomeoneLiked(
                    targetUserId = reviewUserId,
                    fromUserId = currentUserId,
                    fromUserName = Firebase.auth.currentUser?.displayName ?: "Пользователь",
                    itemId = itemId,
                    itemName = _item.value?.name ?: "",
                    reviewId =  _review.value.itemId ,
                    avatarUrl = Firebase.auth.currentUser?.photoUrl?.toString() ?: ""
                )
            }
        }
    }
}