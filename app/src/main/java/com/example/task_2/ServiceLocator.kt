package com.example.task_2

import com.example.task_2.data.remote.KtorClient
import com.example.task_2.data.remote.NobelApiService
import com.example.task_2.data.repository.NobelRepositoryImpl
import com.example.task_2.domain.repository.NobelRepository
import com.example.task_2.domain.usecase.AddFavoritePrizeUseCase
import com.example.task_2.domain.usecase.GetFavoritePrizesUseCase
import com.example.task_2.domain.usecase.GetLaureateDetailUseCase
import com.example.task_2.domain.usecase.GetNobelPrizesUseCase
import com.example.task_2.domain.usecase.LoginUseCase
import com.example.task_2.domain.usecase.RemoveFavoritePrizeUseCase

object ServiceLocator {
    private val apiService = NobelApiService(KtorClient.client)
    private val repository: NobelRepository = NobelRepositoryImpl(apiService)
    val getNobelPrizesUseCase = GetNobelPrizesUseCase(repository)
    val getLaureateDetailUseCase = GetLaureateDetailUseCase(repository)
    val loginUseCase = LoginUseCase(repository)
    val getFavoritePrizesUseCase = GetFavoritePrizesUseCase(repository)
    val addFavoritePrizeUseCase = AddFavoritePrizeUseCase(repository)
    val removeFavoritePrizeUseCase = RemoveFavoritePrizeUseCase(repository)
}
