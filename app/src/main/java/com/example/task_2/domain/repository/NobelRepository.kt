package com.example.task_2.domain.repository

import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.model.LaureateDetail

interface NobelRepository {
    suspend fun getPrizes(year: Int?, category: String?): List<Laureate>
    suspend fun getLaureateDetail(id: String): LaureateDetail
    suspend fun login(username: String, password: String)
    suspend fun getFavoritePrizes(): List<Laureate>
    suspend fun addFavoritePrize(prizeId: Int)
    suspend fun removeFavoritePrize(prizeId: Int)
}
