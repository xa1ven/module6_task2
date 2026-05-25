package com.example.task_2.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.task_2.data.remote.TokenStorage
import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.usecase.AddFavoritePrizeUseCase
import com.example.task_2.domain.usecase.GetFavoritePrizesUseCase
import com.example.task_2.domain.usecase.GetNobelPrizesUseCase
import com.example.task_2.domain.usecase.RemoveFavoritePrizeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NobelListUiState {
    object Loading : NobelListUiState()
    data class Success(val laureates: List<Laureate>) : NobelListUiState()
    data class Error(val message: String) : NobelListUiState()
}

class NobelListViewModel(
    private val getNobelPrizesUseCase: GetNobelPrizesUseCase,
    private val getFavoritePrizesUseCase: GetFavoritePrizesUseCase,
    private val addFavoritePrizeUseCase: AddFavoritePrizeUseCase,
    private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NobelListUiState>(NobelListUiState.Loading)
    val uiState: StateFlow<NobelListUiState> = _uiState

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds

    private var currentYear: Int? = null
    private var currentCategory: String? = null

    init {
        loadPrizes()
        if (TokenStorage.isLoggedIn()) {
            loadFavorites()
        }
    }

    fun loadPrizes(year: Int? = currentYear, category: String? = currentCategory) {
        currentYear = year
        currentCategory = category
        viewModelScope.launch {
            _uiState.value = NobelListUiState.Loading
            try {
                val laureates = getNobelPrizesUseCase(year, category)
                _uiState.value = NobelListUiState.Success(laureates)
            } catch (e: Exception) {
                _uiState.value = NobelListUiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = getFavoritePrizesUseCase()
                val ids = favorites.mapNotNull { it.id.split("_")[0].toIntOrNull() }.toSet()
                _favoriteIds.value = ids
            } catch (_: Exception) {
                // favorites not critical — ignore errors
            }
        }
    }

    fun toggleFavorite(laureateId: String) {
        if (!TokenStorage.isLoggedIn()) return
        val prizeId = laureateId.split("_")[0].toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                if (_favoriteIds.value.contains(prizeId)) {
                    removeFavoritePrizeUseCase(prizeId)
                    _favoriteIds.value = _favoriteIds.value - prizeId
                } else {
                    addFavoritePrizeUseCase(prizeId)
                    _favoriteIds.value = _favoriteIds.value + prizeId
                }
            } catch (_: Exception) {
                // retry silently
            }
        }
    }

    class Factory(
        private val getNobelPrizesUseCase: GetNobelPrizesUseCase,
        private val getFavoritePrizesUseCase: GetFavoritePrizesUseCase,
        private val addFavoritePrizeUseCase: AddFavoritePrizeUseCase,
        private val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NobelListViewModel(
                getNobelPrizesUseCase,
                getFavoritePrizesUseCase,
                addFavoritePrizeUseCase,
                removeFavoritePrizeUseCase
            ) as T
        }
    }
}
