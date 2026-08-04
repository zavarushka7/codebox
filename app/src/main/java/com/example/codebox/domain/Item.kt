package com.example.codebox.domain

import java.util.UUID

/* "договор" между всеми слоями. Все знают про Item, но никто не знает откуда он берется */
data class Item(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String? = "",
    val rating: Int = 0,
    val comment: String? = "",
    val imageUrl: String? = null
)