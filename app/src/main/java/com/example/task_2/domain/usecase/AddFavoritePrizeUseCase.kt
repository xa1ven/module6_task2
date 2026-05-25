package com.example.task_2.domain.usecase

import com.example.task_2.domain.repository.NobelRepository

class AddFavoritePrizeUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(prizeId: Int) = repository.addFavoritePrize(prizeId)
}
