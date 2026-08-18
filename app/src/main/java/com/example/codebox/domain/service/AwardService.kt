package com.example.codebox.domain.service

import com.example.codebox.data.repository.AwardRepository
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.LikeRepository
import com.example.codebox.data.repository.UserReviewRepository
import com.example.codebox.domain.award.Award
import com.example.codebox.domain.award.AwardCondition
import com.example.codebox.domain.award.UserAward
import com.example.codebox.domain.review.UserReview
import com.google.firebase.Timestamp
import javax.inject.Inject

class AwardService @Inject constructor(
    private val awardRepository: AwardRepository,
    private val userReviewRepository: UserReviewRepository,
    private val itemRepository: ItemRepository,
    private val likeRepository: LikeRepository,
    private val notificationService: NotificationService
) {


    suspend fun checkAndAwardUser(userId: String) {

        try {
            val allAwards = awardRepository.getAllAwardDefinitions()
            val userReviews = userReviewRepository.getAllReviewsForUser(userId)
            val userAwards = awardRepository.getAllAwardsForUser(userId)


            allAwards.forEach { award ->
                val currentUserAward = userAwards.find { it.awardKey == award.key }
                val currentRank = currentUserAward?.rank ?: 0


                if (currentRank < award.maxRank) {
                    val currentValue = getConditionValue(award.condition, userId, userReviews)
                    val nextRank = currentRank + 1
                    val thresholdForNextRank = award.getThresholdForRank(nextRank)



                    if (currentValue >= thresholdForNextRank) {

                        unlockAward(userId, award, nextRank)
                    }
                }
            }


        } catch (e: Exception) {

            throw e
        }
    }

    private suspend fun getConditionValue(
        condition: AwardCondition,
        userId: String,
        reviews: List<UserReview>
    ): Int {


        return when (condition) {
            AwardCondition.HATER -> {
                val count = reviews.count { it.rating == 1 }

                count
            }
            AwardCondition.LOVER -> {
                val count = reviews.count { it.rating == 5 }

                count
            }
            AwardCondition.COUNT -> {
                val count = reviews.size

                count
            }
            AwardCondition.GRAPHOMANIAC -> {
                val count = reviews.count { it.comment.length > 200 }

                count
            }
            AwardCondition.LIKES_RECEIVED -> {
                val count = likeRepository.getLikesReceived(userId)

                count
            }
            AwardCondition.LIKES_GIVEN -> {
                val count = likeRepository.getLikesGiven(userId)

                count
            }
            AwardCondition.POLYGLOT -> {
                val languages = getProgrammingLanguages(reviews)
                val count = languages.size

                count
            }
            AwardCondition.UNUSUAL -> {
                val count = getUnusualCount(reviews)

                count
            }
        }
    }

    private suspend fun getProgrammingLanguages(reviews: List<UserReview>): List<String> {
        if (reviews.isEmpty()) return emptyList()

        val allItemIds = reviews.map { it.itemId }.distinct()


        val items = allItemIds.mapNotNull { itemId ->
            val item = itemRepository.getItemById(itemId)
            item
        }

        return items
            .filter { it.type == "язык программирования" }
            .map { it.id }
    }

    private fun getUnusualCount(reviews: List<UserReview>): Int {

        val avgRating = reviews.map { it.rating }.average()


        val count = reviews.count { review ->
            val diff = kotlin.math.abs(review.rating - avgRating)
            val isUnusual = diff >= 2

            isUnusual
        }

        return count
    }

    private suspend fun unlockAward(userId: String, award: Award, rank: Int) {

        try {
            val userAward = UserAward(
                awardKey = award.key,
                userId = userId,
                rank = rank,
                unlockedAt = Timestamp.now()
            )
            awardRepository.saveUserAward(userAward)
            notificationService.notifyAwardUnlocked(
                userId = userId,
                awardName = award.name,
                awardKey = award.key
            )

            if (rank > 1) {
                notificationService.notifyRankUp(
                    userId = userId,
                    awardName = award.name,
                    awardKey = award.key,
                    newRank = rank
                )
            }

        } catch (e: Exception) {

            throw e
        }
    }
}