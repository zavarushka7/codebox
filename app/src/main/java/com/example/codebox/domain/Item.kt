package com.example.codebox.domain

import java.util.UUID

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null
)