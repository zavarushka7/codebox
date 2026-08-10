package com.example.codebox.presentation.profile

import com.example.codebox.domain.UserReview

data class ReviewWithItem(
    val review: UserReview,
    val itemName: String
)
