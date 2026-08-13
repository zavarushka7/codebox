package com.example.codebox.data.repository

import com.example.codebox.domain.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("items")

    fun getItems(): Flow<List<Item>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            try {
                val items = snapshot?.documents?.mapNotNull { doc ->
                    Item(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        type = doc.getString("type") ?: "",
                        description = doc.getString("description") ?: "",
                        symbol = doc.getString("symbol") ?: "",
                        iconKey = doc.getString("iconKey") ?: ""
                    )
                } ?: emptyList()
                trySend(items)
            } catch (e: Exception){
                close(e)
            }
        }
        awaitClose { listener.remove() }
    }


    suspend fun getItemById(itemId: String) : Item? {
        val doc = collection.document(itemId).get().await()
        return if (doc.exists()){
            Item(
                id = doc.getString("id") ?: doc.id,
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description") ?: "",
                symbol = doc.getString("symbol") ?: "",
                iconKey = doc.getString("iconKey") ?: ""
            )
        } else null
    }

    suspend fun search(query: String): List<Item> {
        val lowerQuery = query.trim().lowercase()

        val allItems = collection.get().await().documents.mapNotNull { doc ->
            Item(
                id = doc.getString("id") ?: doc.id,
                name = doc.getString("name") ?: "",
                type = doc.getString("type") ?: "",
                description = doc.getString("description") ?: "",
                symbol = doc.getString("symbol"),
                iconKey = doc.getString("iconKey")
            )
        }

        return allItems.filter { item ->
            item.name.lowercase().contains(lowerQuery)
        }
    }

}