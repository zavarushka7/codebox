package com.example.codebox.domain

import java.util.UUID

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val symbol: String? = null,
    val iconKey: String? = null,
    val averageRating: Double = 0.0
)