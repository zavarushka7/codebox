package com.example.codebox.presentation.create_item

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateItemViewModel @Inject constructor() : ViewModel() {

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _description = mutableStateOf("")
    val description: State<String> = _description

    private val _nameError = mutableStateOf<String?>(null)
    val nameError: State<String?> = _nameError

    fun onNameChange(newName: String) {
        _name.value = newName
        if (newName.isNotBlank()) _nameError.value = null
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun validateAndCreate(): Item? {
        return if (_name.value.isBlank()) {
            _nameError.value = "Название не может быть пустым"
            null
        } else {
            Item(
                id = UUID.randomUUID().toString(),
                name = _name.value.trim(),
                description = _description.value.trim()
            )
        }
    }
}