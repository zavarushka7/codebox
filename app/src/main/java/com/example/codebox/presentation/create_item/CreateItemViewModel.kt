package com.example.codebox.presentation.create_item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

/* Хранит состояние полей ввода*/
@HiltViewModel
class CreateItemViewModel @Inject constructor() : ViewModel() {

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _ratingText = mutableStateOf("")
    val ratingText: State<String> = _ratingText
    private val _nameError = mutableStateOf<String?>(null)
    val nameError: State<String?> = _nameError

    private val _ratingError = mutableStateOf<String?>(null)
    val ratingError: State<String?> = _ratingError

    private val _description = mutableStateOf("")
    val description: State<String> = _description
    private val _descriptionError = mutableStateOf<String?>(null)
    val descriptionError: State<String?> = _descriptionError

    private val _comment = mutableStateOf("")
    val comment: State<String> = _comment
    private val _commentError = mutableStateOf<String?>(null)
    val commentError: State<String?> = _commentError
    fun onNameChange(newName: String) {
        _name.value = newName
        if (newName.isNotBlank()) _nameError.value = null
    }

    fun onRatingChange(newRating: String) {
        _ratingText.value = newRating
        _ratingError.value = null
    }

    fun onDescriptionChange(newDescription: String){
        _description.value = newDescription
        if (newDescription.isNotBlank()) _descriptionError.value = null
    }
    fun onCommentChange(newComment: String){
        _comment.value = newComment
        if (newComment.isNotBlank()) _commentError.value = null
    }
    fun validateAndCreate(): Item? {
        var isValid = true

        if (_name.value.isBlank()) {
            _nameError.value = "Название не может быть пустым"
            isValid = false
        }

        val rating = _ratingText.value.toIntOrNull()
        if (rating == null || rating !in 1..5) {
            _ratingError.value = "Введите число от 1 до 5"
            isValid = false
        }

        return if (isValid) {
            Item(
                id = UUID.randomUUID().toString(),
                name = _name.value.trim(),
                description = null,
                rating = rating!!,
                comment = null,
                imageUrl = null
            )
        } else null
    }
}