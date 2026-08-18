package com.example.codebox.domain.notification

import com.google.firebase.Timestamp

data class Notification(
    val id: String,
    val fromUserId: String, // кому пришло уведомление
    val fromUserName: String = "",
    val avatarUrl: String = "",
    val type: NotificationType = NotificationType.CREATE_NEW_REVIEW,
    val itemId: String = "",
    val itemName: String = "",
    val reviewId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Timestamp = Timestamp.now()
)