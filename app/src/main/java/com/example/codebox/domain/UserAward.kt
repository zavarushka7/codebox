package com.example.codebox.domain

import com.google.firebase.Timestamp

data class UserAward (
    val awardKey: String = "",
    val userId: String = "",
    val rank: Int = 1,
    val unlockedAt: Timestamp = Timestamp.now()
)