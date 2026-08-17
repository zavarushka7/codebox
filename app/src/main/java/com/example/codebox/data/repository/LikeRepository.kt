package com.example.codebox.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
)
{
    suspend fun getLikesGiven(userId: String) : Int {
        return try {
            val snapshot = firestore.collection("user_reviews")
                .whereArrayContains("likedBy", userId)
                .get()
                .await()
        val totalLikes = snapshot.documents.size
            totalLikes
        } catch (e: Exception){
            0
        }
    }
    suspend fun getLikesReceived(userId: String): Int {
        return try {
            val snapshot = firestore.collection("user_reviews")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val totalLikes = snapshot.documents
                .mapNotNull { it.getLong("countLikes")?.toInt() }
                .sum()
            totalLikes
        } catch (e: Exception){
            0
        }
    }
}