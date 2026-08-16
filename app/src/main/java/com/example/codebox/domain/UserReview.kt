package com.example.codebox.domain

data class UserReview(
    val userId: String = "",
    val itemId: String = "",
    val comment: String = "",
    val rating: Int = 0,
    val countLikes: Int = 0
)