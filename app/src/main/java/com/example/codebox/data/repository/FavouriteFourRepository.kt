package com.example.codebox.data.repository

import com.example.codebox.domain.favourite_four.FavouriteFour
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject



class FavouriteFourRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun doc(userId: String) =
        firestore.collection("favourite_four").document(userId)  // ← документ = userId

    suspend fun getFavouriteFour(userId: String): FavouriteFour {
        return try {
            val doc = doc(userId).get().await()
            if (doc.exists()) {
                val favouriteFour = doc.get("favouriteFour") as? List<String> ?: emptyList()
                val itemIds = favouriteFour.mapNotNull { it as? String }
                FavouriteFour(
                    userId = userId,
                    favouriteFour = itemIds
                )
            } else {
                FavouriteFour(userId = userId)
            }
        } catch (e: Exception) {
            FavouriteFour(userId = userId)
        }
    }

    suspend fun saveFavouriteFour(favouriteFour: FavouriteFour) {
        val data = hashMapOf(
            "userId" to favouriteFour.userId,
            "favouriteFour" to favouriteFour.favouriteFour
        )
        doc(favouriteFour.userId).set(data).await()
    }

    // Добавить айтем (максимум 4)
    suspend fun addItem(userId: String, itemId: String): Boolean {
        val current = getFavouriteFour(userId)
        if (current.favouriteFour.size >= 4) return false
        if (current.favouriteFour.contains(itemId)) return false

        val newList = current.favouriteFour + itemId
        saveFavouriteFour(FavouriteFour(userId, newList))
        return true
    }

    // Удалить айтем
    suspend fun removeItem(userId: String, itemId: String) {
        val current = getFavouriteFour(userId)
        val newList = current.favouriteFour.filter { it != itemId }
        saveFavouriteFour(FavouriteFour(userId, newList))
    }

    // Проверить, полный ли список (4 айтема)
    suspend fun isFull(userId: String): Boolean {
        val current = getFavouriteFour(userId)
        return current.favouriteFour.size >= 4
    }

    // Получить количество айтемов
    suspend fun count(userId: String): Int {
        val current = getFavouriteFour(userId)
        return current.favouriteFour.size
    }
}