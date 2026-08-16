package com.example.codebox.domain

data class ReviewWithAuthor(
    val userId: String = "",
    val itemId: String = "",
    val comment: String = "",
    val rating: Int = 0,
    val authorName: String = "",
    val avatarUrl: String = "",
    val countLikes: Int = 0,
    val likedBy: List<String>
)