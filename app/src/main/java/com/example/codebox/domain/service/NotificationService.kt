package com.example.codebox.domain.service

import com.example.codebox.data.repository.NotificationRepository
import com.example.codebox.domain.notification.Notification
import com.example.codebox.domain.notification.NotificationType
import com.google.firebase.Timestamp
import javax.inject.Inject

class NotificationService @Inject constructor(
    private val notificationRepository: NotificationRepository
) {

    suspend fun notifyReviewCreated(
        userId: String,
        itemName: String,
        itemId: String,
        rating: Int,
        comment: String = ""
    ) {
        val notification = Notification(
            id = "",
            fromUserId = userId,
            fromUserName = "Вы",
            avatarUrl = "",
            type = NotificationType.CREATE_NEW_REVIEW,
            itemId = itemId,
            itemName = itemName,
            reviewId = "${userId}_$itemId",  // ← ФОРМИРУЕМ ID ОТЗЫВА
            rating = rating,
            comment = comment,
            createdAt = Timestamp.now()
        )
        notificationRepository.saveNotification(notification)
    }

    suspend fun notifySomeoneLiked(
        targetUserId: String,
        fromUserId: String,
        fromUserName: String,
        itemId: String,
        itemName: String,
        reviewId: String,
        avatarUrl: String = ""
    ) {
        if (targetUserId == fromUserId) return

        val notification = Notification(
            id = "",
            fromUserId = targetUserId,
            fromUserName = fromUserName,
            avatarUrl = avatarUrl,
            type = NotificationType.SOMEONE_LIKED,
            itemId = itemId,
            itemName = itemName,
            reviewId = reviewId,  // ← УЖЕ ПЕРЕДАЕТСЯ ИЗ ТОГГЛА
            rating = 0,
            comment = "",
            createdAt = Timestamp.now()
        )
        notificationRepository.saveNotification(notification)
    }

    suspend fun notifyAwardUnlocked(
        userId: String,
        awardName: String,
        awardKey: String
    ) {
        val notification = Notification(
            id = "",
            fromUserId = userId,
            fromUserName = "Вы",
            avatarUrl = "",
            type = NotificationType.AWARD_UNLOCKED,
            itemId = awardKey,
            itemName = awardName,
            reviewId = "",  // Для наград reviewId не нужен
            rating = 0,
            comment = "",
            createdAt = Timestamp.now()
        )
        notificationRepository.saveNotification(notification)
    }

    suspend fun notifyRankUp(
        userId: String,
        awardName: String,
        awardKey: String,
        newRank: Int
    ) {
        val notification = Notification(
            id = "",
            fromUserId = userId,
            fromUserName = "Вы",
            avatarUrl = "",
            type = NotificationType.RANK_UP,
            itemId = awardKey,
            itemName = awardName,
            reviewId = "",  // Для наград reviewId не нужен
            rating = 0,
            comment = "ранг $newRank",
            createdAt = Timestamp.now()
        )
        notificationRepository.saveNotification(notification)
    }
}