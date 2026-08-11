package com.example.codebox.data.repository

import com.example.codebox.domain.UserReview
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserReviewRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun doc(userId: String, itemId: String) =
        firestore.collection("user_reviews").document("${userId}_${itemId}")

    suspend fun getReview(userId: String, itemId: String): UserReview? {
        val doc = doc(userId, itemId).get().await()
        return if (doc.exists()){
            UserReview(
                userId = doc.getString("userId") ?: userId,
                itemId = doc.getString("itemId") ?: itemId,
                comment = doc.getString("comment") ?: "",
                rating = doc.getLong("rating")?.toInt() ?: 0
            )
        } else null
    }

    suspend fun saveReview(review: UserReview) {
        doc(review.userId, review.itemId).set(review).await()
    }

    suspend fun getAllReviewsForUser(userId: String): List<UserReview>{
        return firestore.collection("user_reviews")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                UserReview(
                    userId = doc.getString("userId") ?: "",
                    itemId = doc.getString("itemId") ?: "",
                    comment = doc.getString("comment") ?: "",
                    rating = doc.getLong("rating")?.toInt() ?: 0
                )
            }
    }

    suspend fun deleteReview(userId: String, itemId: String){
        doc(userId, itemId).delete().await()
    }
}