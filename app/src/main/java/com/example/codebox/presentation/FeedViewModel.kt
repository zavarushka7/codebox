package com.example.codebox.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: ItemRepository
): ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    init {
        viewModelScope.launch {
            repository.getItems().collect { list ->
                _items.value = list
            }
        }
    }
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    fun addItem(item: Item){
        viewModelScope.launch {
            repository.addItem(item)
        }
    }
}