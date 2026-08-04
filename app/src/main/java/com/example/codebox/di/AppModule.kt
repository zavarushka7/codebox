package com.example.codebox.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/* AppModule - фабрика. Hilt не знает как создать FirebaseFirestore (это не мой класс,
* а библиотечный), поэтому я пишу Provides. Теперь когда любой класс запросит
* FirebaseFirestore в конструкторе, Hilt поймет откуда его взять
*  */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}