package com.example.codebox.presentation.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.data.repository.SettingsRepository
import com.example.codebox.domain.text_style.TextCaseStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val textCaseStyle: StateFlow<TextCaseStyle> = settingsRepository.textCaseStyle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TextCaseStyle.NORMAL)

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
            CatalogMode.SEARCH, CatalogMode.FAVOURITE_SELECT -> {  // ← ДОБАВЛЕН FAVOURITE_SELECT
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
            else -> {}
        }
    }

    fun onQueryChange(newQuery: String) {
        if (mode != CatalogMode.SEARCH && mode != CatalogMode.FAVOURITE_SELECT) return  // ← ДОБАВЛЕН FAVOURITE_SELECT
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