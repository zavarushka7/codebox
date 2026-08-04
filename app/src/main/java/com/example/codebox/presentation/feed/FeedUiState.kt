package com.example.codebox.presentation.feed

import com.example.codebox.domain.Item

/* sealed class - запечатанный тип, у него ограниченное число вариантов.
* UI делает when(state) и обязан обработать все три случая: загрузка, успех, ошибка
* Это защищает от ситуации "а что если данные null?"*/
sealed class FeedUiState {
    data object Loading: FeedUiState()
    data class Success(val items: List<Item>) : FeedUiState()
    data class Error(val message: String): FeedUiState()
}