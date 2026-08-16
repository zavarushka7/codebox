package com.example.codebox.data.repository

import com.example.codebox.domain.ReviewWithAuthor
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
                rating = doc.getLong("rating")?.toInt() ?: 0,
                countLikes = doc.getLong("countLikes")?.toInt() ?: 0

            )
        } else null
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
                    rating = doc.getLong("rating")?.toInt() ?: 0,
                    countLikes = doc.getLong("countLikes")?.toInt() ?: 0

                )
            }
    }


    suspend fun getReviewsForItem(itemId: String): List<ReviewWithAuthor>{
        val snapshot = firestore.collection("user_reviews")
            .whereEqualTo("itemId", itemId)
            .get()
            .await()

        if (snapshot.isEmpty) return emptyList()
        return snapshot.documents.mapNotNull { doc ->
            val userId = doc.getString("userId") ?: return@mapNotNull null
            val review = UserReview(
                userId = userId,
                itemId = doc.getString("itemId") ?: "",
                comment = doc.getString("comment") ?: "",
                rating = doc.getLong("rating")?.toInt() ?: 0,
                countLikes = doc.getLong("countLikes")?.toInt() ?: 0
            )
            // подтягиваем nickname из users/{uid}
            val userDoc = firestore.collection("users").document(userId).get().await()
            val nickname = userDoc.getString("nickname") ?: userId.take(6)
            val avatarData = userDoc.getString("avatarBase64") ?: ""

            ReviewWithAuthor(
                userId = review.userId,
                itemId = review.itemId,
                comment = review.comment,
                rating = review.rating,
                authorName = nickname,
                avatarUrl = avatarData,
                countLikes = review.countLikes
            )
        }
    }
    suspend fun getAverageRating(itemId: String): Double {
        val snapshot = firestore.collection("user_reviews")
            .whereEqualTo("itemId", itemId)
            .get()
            .await()

        if (snapshot.isEmpty) return 0.0

        val ratings = snapshot.documents.mapNotNull { it.getLong("rating")?.toInt() }
        return if (ratings.isNotEmpty()) ratings.average() else 0.0
    }

    suspend fun saveReview(review: UserReview) {
        doc(review.userId, review.itemId).set(review).await()

        // Пересчитываем и пишем в документ item
        val newAvg = getAverageRating(review.itemId)
        firestore.collection("items").document(review.itemId)
            .update("averageRating", newAvg)
            .await()
    }

    suspend fun deleteReview(userId: String, itemId: String) {
        doc(userId, itemId).delete().await()

        // Пересчитываем после удаления
        val newAvg = getAverageRating(itemId)
        firestore.collection("items").document(itemId)
            .update("averageRating", newAvg)
            .await()
    }
}