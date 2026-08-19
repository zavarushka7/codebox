package com.example.codebox.data.repository

import android.util.Log
import com.example.codebox.domain.award.Award
import com.example.codebox.domain.award.AwardCondition
import com.example.codebox.domain.award.UserAward
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AwardRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getAllAwardDefinitions(): List<Award> {

        return try {
            val snapshot = firestore.collection("awards")
                .orderBy("order")
                .get()
                .await()



            if (snapshot.isEmpty) {
                return emptyList()
            }

            snapshot.documents.mapNotNull { doc ->
                try {
                    val key = doc.getString("key") ?: return@mapNotNull null
                    val name = doc.getString("name") ?: ""
                    val iconKey = doc.getString("iconKey") ?: ""
                    val conditionStr = doc.getString("condition") ?: "COUNT"
                    val order = doc.getLong("order")?.toInt() ?: 0
                    val maxRank = doc.getLong("maxRank")?.toInt() ?: 1

                    val rankThresholdsRaw = doc.get("rankThresholds")
                    val rankNamesRaw = doc.get("rankNames")
                    val rankDescriptionsRaw = doc.get("rankDescriptions")


                    val rankThresholds = parseRankArray(rankThresholdsRaw)
                    val rankNames = parseRankStringArray(rankNamesRaw)
                    val rankDescriptions = parseRankStringArray(rankDescriptionsRaw)



                    val condition = try {
                        AwardCondition.valueOf(conditionStr.uppercase())
                    } catch (e: IllegalArgumentException) {
                        AwardCondition.COUNT
                    }

                    Award(
                        key = key,
                        name = name,
                        iconKey = iconKey,
                        condition = condition,
                        order = order,
                        maxRank = maxRank,
                        rankThresholds = rankThresholds,
                        rankDescriptions = rankDescriptions
                    )
                } catch (e: Exception) {

                    null
                }

            }
        } catch (e: Exception) {

            emptyList()
        }
    }

    private fun parseRankArray(value: Any?): List<Int> {
        return when (value) {
            is List<*> -> value.mapNotNull {
                when (it) {
                    is Long -> it.toInt()
                    is Int -> it
                    is Double -> it.toInt()
                    else -> null
                }
            }
            is String -> value.split(",").mapNotNull { it.trim().toIntOrNull() }
            else -> emptyList()
        }
    }

    private fun parseRankStringArray(value: Any?): List<String> {
        return when (value) {
            is List<*> -> value.mapNotNull { it?.toString() }
            is String -> value.split(",").map { it.trim() }
            else -> emptyList()
        }
    }

    suspend fun getAllAwardsForUser(userId: String): List<UserAward> {


        return try {
            val snapshot = firestore.collection("user_awards")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val awardKey = doc.getString("awardKey") ?: return@mapNotNull null
                    UserAward(
                        awardKey = awardKey,
                        userId = doc.getString("userId") ?: userId,
                        rank = doc.getLong("rank")?.toInt() ?: 1,
                        unlockedAt = doc.getTimestamp("unlockedAt") ?: Timestamp.now()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveUserAward(award: UserAward) {
        try {
            val data = hashMapOf(
                "awardKey" to award.awardKey,
                "userId" to award.userId,
                "rank" to award.rank,
                "unlockedAt" to award.unlockedAt
            )
            val docRef = firestore.collection("user_awards")
                .document("${award.userId}_${award.awardKey}")
            docRef.set(data).await()
        } catch (e: Exception) {

        }
    }
}