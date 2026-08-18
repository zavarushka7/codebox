package com.example.codebox.data.repository

import com.example.codebox.domain.notification.Notification
import com.example.codebox.domain.notification.NotificationType
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("notification")
    fun getNotificationsForUser(userId: String) : Flow<List<Notification>> = callbackFlow {
        val listener = firestore.collection("notification")
            .whereEqualTo("fromUserId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    close(exception)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Notification(
                            id = doc.id,
                            fromUserId = doc.getString("fromUserId") ?: "",
                            fromUserName = doc.getString("fromUserName") ?: "",
                            avatarUrl = doc.getString("avatarUrl") ?: "",
                            type = try {
                                NotificationType.valueOf(doc.getString("type") ?: "CREATE_NEW_REVIEW")
                            } catch (e: IllegalArgumentException) {
                                NotificationType.CREATE_NEW_REVIEW
                            },
                            itemId = doc.getString("itemId") ?: "",
                            itemName = doc.getString("itemName") ?: "",
                            reviewId = doc.getString("reviewId") ?: "",
                            rating = doc.getLong("rating")?.toInt() ?: 0,
                            comment = doc.getString("comment") ?: "",
                            createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                        )
                    } catch (e: Exception){
                        null
                    }
                } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }

    }
    suspend fun saveNotification(notification: Notification) {
        try {
            val data = hashMapOf(
                "fromUserId" to notification.fromUserId,
                "fromUserName" to notification.fromUserName,
                "avatarUrl" to notification.avatarUrl,
                "type" to notification.type.name,
                "itemId" to notification.itemId,
                "itemName" to notification.itemName,
                "reviewId" to notification.reviewId,
                "rating" to notification.rating,
                "comment" to notification.comment,
                "createdAt" to notification.createdAt
            )
            collection.add(data).await()
        } catch (e: Exception) {
            // Обработка ошибки
        }
    }
}