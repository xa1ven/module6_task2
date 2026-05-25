package com.example.task_2.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.task_2.domain.model.LaureateDetail
import com.example.task_2.domain.usecase.GetLaureateDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NobelDetailUiState {
    object Loading : NobelDetailUiState()
    data class Success(val detail: LaureateDetail) : NobelDetailUiState()
    data class Error(val message: String) : NobelDetailUiState()
}

class NobelDetailViewModel(
    private val getLaureateDetailUseCase: GetLaureateDetailUseCase,
    private val laureateId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<NobelDetailUiState>(NobelDetailUiState.Loading)
    val uiState: StateFlow<NobelDetailUiState> = _uiState

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = NobelDetailUiState.Loading
            try {
                val detail = getLaureateDetailUseCase(laureateId)
                _uiState.value = NobelDetailUiState.Success(detail)
            } catch (e: Exception) {
                _uiState.value = NobelDetailUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    class Factory(
        private val useCase: GetLaureateDetailUseCase,
        private val laureateId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NobelDetailViewModel(useCase, laureateId) as T
        }
    }
}
