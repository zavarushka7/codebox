package com.example.codebox.presentation.notification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.NotificationRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.domain.notification.Notification
import com.example.codebox.domain.notification.NotificationDisplay
import com.example.codebox.domain.notification.NotificationType
import com.example.codebox.domain.service.NotificationService
import com.example.codebox.domain.text_style.TextCaseStyle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel  @Inject constructor(
    private val notificationRepository: NotificationRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val userId = Firebase.auth.currentUser?.uid ?: ""

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            notificationRepository.getNotificationsForUser(userId)
                .collect { notifications ->
                    val displays = notifications.map {  notification ->
                        NotificationDisplay(
                            notification = notification,
                            timeAgo = getTimeAgo(notification.createdAt.toDate()),
                            message = getMessageForType(notification),
                            isToday = isToday(notification.createdAt.toDate()),
                            avatarUrl = notification.avatarUrl,
                            reviewId = notification.reviewId,
                                    fromUserName = notification.fromUserName
                        )
                    }
                    _uiState.update {
                        it.copy(
                            notifications = displays,
                            isLoading = false
                        )
                    }
                }
        }
    }
    private fun getMessageForType(notification: Notification): String{
        return when (notification.type) {
            NotificationType.CREATE_NEW_REVIEW -> {
                "вы написали ревью"
            }
            NotificationType.SOMEONE_LIKED -> {
                "${notification.fromUserName} поставил лайк вашему ревью на ${notification.itemName}"
            }
            NotificationType.AWARD_UNLOCKED ->  {
                "вы получили награду ${notification.itemName}"
            }
            NotificationType.RANK_UP -> {
                "ваша награда ${notification.itemName} повысилась в ранге до ${notification.comment}"
            }
        }
    }
    private fun getTimeAgo(date: Date): String{
        val now = Date()
        val diff = now.time - date.time

        return when {
            diff < 60_000 -> "только что"
            diff < 3_600_000 -> "${diff / 3600000} ч назад"
            diff < 172800000 -> "вчера"
            else -> {
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                format.format(date)
            }
        }
    }
    private fun isToday(date: Date): Boolean {
        val today = Date()
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return format.format(date) == format.format(today)
    }

}