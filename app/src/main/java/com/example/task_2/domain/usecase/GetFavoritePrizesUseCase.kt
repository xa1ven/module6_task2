package com.example.task_2.domain.usecase

import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.repository.NobelRepository

class GetFavoritePrizesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(): List<Laureate> = repository.getFavoritePrizes()
}
