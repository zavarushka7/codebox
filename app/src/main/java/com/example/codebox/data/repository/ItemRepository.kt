package com.example.codebox.data.repository

import com.example.codebox.domain.Item
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/* Единственный класс, который знает про Firestore. Остальные про него не знают  */
class ItemRepository @Inject constructor(
    private val firestore: FirebaseFirestore ) {
    private val collection = firestore.collection("items")

    // возвращает Flow<List<Item>> - это не просто список, а поток, который обновляется сам
    // callbackFlow{} - обертка, которая превращает Firebase-Callback в Kotlin-Flow.
    fun getItems() : Flow<List<Item>>  = callbackFlow{
        // addSnapshotListener - подписка на коллекцию в реальном времени. если кто-то добавит документ, Firestore пришлет новый snapshot
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
            // trySend(items) - отправляет данные всем, кто слушает
            trySend(items)
        }
        // awaitClose { listener.remove() } - когда никто не слушает Flow (например, экран уничтожен), отписка от Firstore. Без этого утечка памяти
        awaitClose {
            listener.remove()
        }
    }
    suspend fun addItem(item: Item){
        // await() - превращает Firebase-таску в suspend-функцию, чтобы можно было вызвать из корутины
        collection.document(item.id).set(item).await()
    }

}