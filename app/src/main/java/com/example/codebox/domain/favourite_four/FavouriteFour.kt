package com.example.codebox.domain.favourite_four

import com.example.codebox.domain.Item

data class FavouriteFour(
    val userId: String = "",
    val favouriteFour: List<String> = emptyList()
)
