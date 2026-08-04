package com.example.codebox.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codebox.data.repository.ItemRepository
import com.example.codebox.domain.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/* Hilt сам создает ViewModel и подсовывает ItemRepository*/
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: ItemRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    init {
        // при создании ViewModel сразу запускается корутина
        viewModelScope.launch {
            repository.getItems()
                // catch{] - если Flow выбросит ошибку (например, нет прав доступа) ловим и показываем FeedUiState.Error
                .catch { e ->
                    _uiState.value = FeedUiState.Error(e.message ?: "Ошибка загрузки")
                }
                // collect{} - подписывается на Flow из репозитория. Каждый раз, когда Firestore пришлет новый список, _uiState обновляется
                .collect { list ->
                    _uiState.value = FeedUiState.Success(list)
            }
        }
    }


    fun addItem(item: Item){
        // viewModelScope - корутины, привязанные к жизни ViewModel. Если ViewModel уничтожен (например, поворот экрана), корутина отменится
        viewModelScope.launch {
            try {
                repository.addItem(item)
            } catch (e: Exception){
                _uiState.value = FeedUiState.Error("Не удалось сохранить: ${e.message}")
            }

        }
    }
}