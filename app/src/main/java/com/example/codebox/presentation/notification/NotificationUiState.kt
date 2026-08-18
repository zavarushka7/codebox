package com.example.codebox.presentation.notification

import com.example.codebox.domain.notification.NotificationDisplay

data class NotificationUiState (
    val notifications: List<NotificationDisplay> = emptyList(),
    val isLoading: Boolean = false
)

