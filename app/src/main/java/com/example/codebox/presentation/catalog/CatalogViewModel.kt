package com.example.codebox.presentation.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(FlowPreview::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val mode: CatalogMode
    val typeFilter: String

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Idle)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        mode = when {
            savedStateHandle.contains("mode") -> {
                try {
                    CatalogMode.valueOf(savedStateHandle["mode"]!!)
                } catch (e: IllegalArgumentException) {
                    CatalogMode.SEARCH
                }
            }
            savedStateHandle.contains("type") -> CatalogMode.BY_TYPE
            else -> CatalogMode.SEARCH
        }

        typeFilter = savedStateHandle.get<String>("type") ?: ""

        when (mode) {
            CatalogMode.SEARCH -> {
                _query
                    .debounce(300)
                    .filter { it.isNotBlank() }
                    .onEach { performSearch(it) }
                    .launchIn(viewModelScope)
            }
            CatalogMode.TYPES_LIST -> loadTypes()
            CatalogMode.BY_TYPE -> loadByType(typeFilter)
            CatalogMode.TOP_RATED -> loadTopRated()
            CatalogMode.LOWEST_RATED -> loadLowestRated()
        }
    }

    fun onQueryChange(newQuery: String) {
        if (mode != CatalogMode.SEARCH) return
        _query.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = CatalogUiState.Idle
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val results = itemRepository.search(query)
                _uiState.value = CatalogUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.message ?: "search failed")
            }
        }
    }

    private fun loadTypes() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val types = itemRepository.getTypes()
                _uiState.value = CatalogUiState.TypesSuccess(types)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.message ?: "load failed")
            }
        }
    }

    private fun loadByType(type: String) {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val all = itemRepository.getItems().first()
                val filtered = if (type.isBlank()) {
                    all
                } else {
                    all.filter { it.type.equals(type, ignoreCase = true) }
                }
                _uiState.value = CatalogUiState.Success(filtered)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.message ?: "load failed")
            }
        }
    }

    private fun loadTopRated() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val all = itemRepository.getItems().first()
                val sorted = all.sortedByDescending { it.averageRating }
                _uiState.value = CatalogUiState.Success(sorted)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.message ?: "load failed")
            }
        }
    }

    private fun loadLowestRated() {
        viewModelScope.launch {
            _uiState.value = CatalogUiState.Loading
            try {
                val all = itemRepository.getItems().first()
                val sorted = all.sortedBy { it.averageRating }
                _uiState.value = CatalogUiState.Success(sorted)
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error(e.message ?: "load failed")
            }
        }
    }
}