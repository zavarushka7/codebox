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
        val snapshot = doc(userId, itemId).get().await()
        return snapshot.toObject(UserReview::class.java)
    }

    suspend fun saveReview(review: UserReview) {
        doc(review.userId, review.itemId).set(review).await()
    }
}