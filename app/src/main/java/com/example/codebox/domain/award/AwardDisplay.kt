package com.example.codebox.domain.award

import com.google.firebase.Timestamp

data class AwardDisplay(
    val award: Award,
    val currentRank: Int = 0,
    val maxRank: Int = 1,
    val progress: Int = 0,
    val nextThreshold: Int = 0,
    val isMaxRank: Boolean = false,
    val isUnlocked: Boolean = false,
    val unlockedAt: Timestamp? = null
) {

    // Текущее описание (с учетом ранга)
    val currentDescription: String
        get() = award.getRankDescription(currentRank)

    // Проверка, есть ли описание
    val hasDescription: Boolean
        get() = currentDescription.isNotBlank()
}