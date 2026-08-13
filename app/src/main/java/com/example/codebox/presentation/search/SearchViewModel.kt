package com.example.codebox.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject



@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        _query
            .debounce(300)                    // ждём 300мс после последней буквы
            .filter { it.isNotBlank() }       // игнорируем пустую строку
            .onEach { performSearch(it) }     // запускаем поиск
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val results = itemRepository.search(query)
                _uiState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "search failed")
            }
        }
    }
}