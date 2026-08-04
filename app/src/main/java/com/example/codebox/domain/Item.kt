package com.example.codebox.domain

data class Item(
    val id: String = "",
    val name: String = "",
    val rating: Int = 0,
    val imageUrl: String? = null
)