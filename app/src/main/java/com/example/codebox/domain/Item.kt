package com.example.codebox.domain

data class Item(
    val id: String,
    val name: String,
    val rating: Int,
    val imageUrl: String? = null
)