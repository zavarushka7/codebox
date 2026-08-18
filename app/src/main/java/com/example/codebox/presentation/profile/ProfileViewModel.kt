package com.example.codebox.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.AwardRepository
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.LikeRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.award.AwardCondition
import com.example.codebox.domain.award.AwardDisplay
import com.example.codebox.domain.review.ReviewWithItem
import com.example.codebox.domain.text_style.TextCaseStyle
import com.example.codebox.domain.service.AwardService
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
    settingsRepository: SettingsRepository,
    private val awardService: AwardService,
    private val awardRepository: AwardRepository,
    private val likeRepository: LikeRepository
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
                loadUserData()
                awardService.checkAndAwardUser(profileUserId)
                loadAwards()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "ошибка загрузки профиля")
            }
        }
    }

    private suspend fun loadUserData() {
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

            val email = userDoc.getString("email")
                ?: if (profileUserId == Firebase.auth.currentUser?.uid) {
                    Firebase.auth.currentUser?.email
                } else null
                    ?: ""

            _uiState.value = ProfileUiState.Success(
                email = email,
                nickname = nickname,
                avatarUrl = avatarUrl,
                description = description,
                reviews = reviewsWithItems,
                awardsUiState = AwardsUiState.Loading  // ← изменено
            )

        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun loadAwards() {
        try {
            val allAwards = awardRepository.getAllAwardDefinitions()

            if (allAwards.isEmpty()) {
                val currentState = _uiState.value
                if (currentState is ProfileUiState.Success) {
                    _uiState.value = currentState.copy(
                        awardsUiState = AwardsUiState.Empty
                    )
                }
                return
            }

            val userAwards = awardRepository.getAllAwardsForUser(profileUserId)
            val userReviews = userReviewRepository.getAllReviewsForUser(profileUserId)

            val awardDisplays = allAwards.map { award ->
                val userAward = userAwards.find { it.awardKey == award.key }
                val currentRank = userAward?.rank ?: 0
                val isUnlocked = currentRank > 0

                val currentValue = when (award.condition) {
                    AwardCondition.HATER -> userReviews.count { it.rating == 1 }
                    AwardCondition.LOVER -> userReviews.count { it.rating == 5 }
                    AwardCondition.COUNT -> userReviews.size
                    AwardCondition.GRAPHOMANIAC -> userReviews.count { it.comment.length > 200 }
                    AwardCondition.LIKES_RECEIVED -> likeRepository.getLikesReceived(profileUserId)
                    AwardCondition.LIKES_GIVEN -> likeRepository.getLikesGiven(profileUserId)
                    AwardCondition.POLYGLOT -> userReviews.map { it.itemId }.distinct().size
                    AwardCondition.UNUSUAL -> {
                        if (userReviews.size < 2) 0
                        else {
                            val avgRating = userReviews.map { it.rating }.average()
                            userReviews.count { review ->
                                kotlin.math.abs(review.rating - avgRating) >= 2
                            }
                        }
                    }
                }

                val nextThreshold = if (currentRank < award.maxRank) {
                    award.getThresholdForRank(currentRank + 1)
                } else {
                    currentValue
                }

                val isMaxRank = currentRank >= award.maxRank

                AwardDisplay(
                    award = award,
                    currentRank = currentRank,
                    maxRank = award.maxRank,
                    progress = currentValue.coerceAtMost(nextThreshold),
                    nextThreshold = nextThreshold,
                    isMaxRank = isMaxRank,
                    isUnlocked = isUnlocked,
                    unlockedAt = userAward?.unlockedAt
                )
            }

            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                _uiState.value = currentState.copy(
                    awardsUiState = if (awardDisplays.isEmpty()) {
                        AwardsUiState.Empty
                    } else {
                        AwardsUiState.Loaded(awardDisplays)
                    }
                )
            }

        } catch (e: Exception) {
            // При ошибке показываем пустое состояние
            val currentState = _uiState.value
            if (currentState is ProfileUiState.Success) {
                _uiState.value = currentState.copy(
                    awardsUiState = AwardsUiState.Empty
                )
            }
            throw e
        }
    }

    fun deleteReview(itemId: String) {
        viewModelScope.launch {
            try {
                val userId = Firebase.auth.currentUser?.uid ?: return@launch
                userReviewRepository.deleteReview(userId, itemId)
                loadProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("не удалось удалить: ${e.message}")
            }
        }
    }
}