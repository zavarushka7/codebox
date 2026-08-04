package com.example.codebox.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CreateItemViewModel @Inject constructor() : ViewModel() {
    var name = mutableStateOf("")
        private set
    var ratingText = mutableStateOf("")
        private set
    fun onNameChange(newName: String){
        name.value = newName
    }
    fun onRatingChange(newRating: String){
        ratingText.value = newRating
    }

    fun createItem() : Item{
        return Item(
            id = System.currentTimeMillis().toString(),
            name = name.value,
            rating = ratingText.value.toIntOrNull() ?: 0
        )
    }
}