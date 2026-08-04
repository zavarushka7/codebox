package com.example.codebox.presentation

import androidx.lifecycle.ViewModel
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(): ViewModel() {
    private val _items = MutableStateFlow(
        listOf(
            Item("1", "Kotlin", 5),
            Item("2", "Python", 4)
        )
    )
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    fun addItem(item: Item){
        _items.value = _items.value + item
    }
}