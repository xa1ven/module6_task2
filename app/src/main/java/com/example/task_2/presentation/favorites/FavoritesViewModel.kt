package com.example.task_2.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.usecase.GetFavoritePrizesUseCase
import com.example.task_2.domain.usecase.RemoveFavoritePrizeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val laureates: List<Laureate>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

class FavoritesViewModel(
    private val getFavoritePrizesUseCase: GetFavoritePrizesUseCase,
    private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            try {
                val laureates = getFavoritePrizesUseCase()
                _uiState.value = FavoritesUiState.Success(laureates)
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun removeFavorite(laureateId: String) {
        val prizeId = laureateId.split("_")[0].toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                removeFavoritePrizeUseCase(prizeId)
                loadFavorites()
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    class Factory(
        private val getFavoritePrizesUseCase: GetFavoritePrizesUseCase,
        private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(getFavoritePrizesUseCase, removeFavoritePrizeUseCase) as T
        }
    }
}
