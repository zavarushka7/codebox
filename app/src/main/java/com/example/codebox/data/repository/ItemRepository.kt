package com.example.codebox.data.repository

import com.example.codebox.domain.Item
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ItemRepository @Inject constructor() {
    private val collection = Firebase.firestore.collection("items")

    fun getItems() : Flow<List<Item>>  = callbackFlow{
        val listener = collection.addSnapshotListener { snapshot, exception ->
            if (exception != null){
                close(exception)
                return@addSnapshotListener
            }
            val items = if (snapshot != null && !snapshot.isEmpty ) {
                snapshot.toObjects(Item::class.java)
            } else {
                emptyList()
            }
            trySend(items)
        }
        awaitClose {
            listener.remove()
        }
    }
    suspend fun addItem(item: Item){
        collection.document(item.id).set(item).await()
    }

}