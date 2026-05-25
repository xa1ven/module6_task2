package com.example.task_2.domain.usecase

import com.example.task_2.domain.model.LaureateDetail
import com.example.task_2.domain.repository.NobelRepository

class GetLaureateDetailUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(id: String): LaureateDetail {
        return repository.getLaureateDetail(id)
    }
}
