package com.example.codebox.domain.notification

data class NotificationDisplay(
    val notification: Notification,
    val timeAgo: String = "",
    val message: String = "",
    val isToday: Boolean = false,
    val avatarUrl: String = "",
    val reviewId: String = "",
    val fromUserName: String = ""
)