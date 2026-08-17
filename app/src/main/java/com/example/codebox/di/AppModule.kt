package com.example.codebox.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.codebox.data.repository.AwardRepository
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.LikeRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.service.AwardService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore


    @Provides
    @Singleton
    fun provideAwardService(
        awardRepository: AwardRepository,
        userReviewRepository: UserReviewRepository,
        itemRepository: ItemRepository,
        likeRepository: LikeRepository
    ): AwardService {
        return AwardService(
            awardRepository, userReviewRepository,
            itemRepository, likeRepository
        )
    }
}