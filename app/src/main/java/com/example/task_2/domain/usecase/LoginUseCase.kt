package com.example.task_2.domain.usecase

import com.example.task_2.domain.repository.NobelRepository

class LoginUseCase(private val repository: NobelRepository) {
    suspend operator fun invoke(username: String, password: String) {
        repository.login(username, password)
    }
}
