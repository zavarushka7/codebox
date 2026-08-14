package com.example.codebox.presentation.catalog

import com.example.codebox.domain.Item

sealed class CatalogUiState {
    object Idle : CatalogUiState()
    object Loading : CatalogUiState()
    data class Success(val items: List<Item>) : CatalogUiState()
    data class TypesSuccess(val types: List<String>) : CatalogUiState()
    data class Error(val message: String) : CatalogUiState()
}
