package com.example.task_2.domain.usecase

import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.repository.NobelRepository

class GetNobelPrizesUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(year: Int?, category: String?): List<Laureate> {
        return repository.getPrizes(year, category)
    }
}
