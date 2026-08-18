package com.example.codebox.domain.notification

enum class NotificationType {
    CREATE_NEW_REVIEW, // пользователь создал ревью
    SOMEONE_LIKED, // кто то лайкнул ревью пользователя
    AWARD_UNLOCKED, // получена награда
    RANK_UP // повышение ранга награды
}