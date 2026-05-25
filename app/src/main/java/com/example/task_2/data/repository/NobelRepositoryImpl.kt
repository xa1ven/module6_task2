package com.example.task_2.data.repository

import com.example.task_2.data.remote.NobelApiService
import com.example.task_2.data.remote.TokenStorage
import com.example.task_2.data.remote.dto.PrizeDto
import com.example.task_2.domain.model.Laureate
import com.example.task_2.domain.model.LaureateDetail
import com.example.task_2.domain.repository.NobelRepository

class NobelRepositoryImpl(private val apiService: NobelApiService) : NobelRepository {

    override suspend fun getPrizes(year: Int?, category: String?): List<Laureate> {
        val prizes = apiService.getPrizes()
        return prizes
            .filter { year == null || it.awardYear == year.toString() }
            .filter { category == null || it.category.equals(category, ignoreCase = true) }
            .flatMap { prize -> mapPrizeToLaureates(prize) }
    }

    override suspend fun getLaureateDetail(id: String): LaureateDetail {
        val parts = id.split("_")
        val prizeId = parts[0].toIntOrNull() ?: 0
        val laureateId = parts.getOrNull(1)?.toIntOrNull()
        val prize = apiService.getPrize(prizeId)
        val laureate = prize.laureates.find { it.id == laureateId }
        return LaureateDetail(
            id = id,
            fullName = laureate?.fullName ?: prize.fullName,
            year = prize.awardYear,
            category = prize.category,
            motivation = laureate?.motivation ?: prize.motivation,
            birthCountry = "",
            portraitUrl = laureate?.portraitUrl ?: ""
        )
    }

    override suspend fun login(username: String, password: String) {
        val response = apiService.login(username, password)
        TokenStorage.token = response.token
    }

    override suspend fun getFavoritePrizes(): List<Laureate> {
        val prizes = apiService.getFavoritePrizes()
        return prizes.flatMap { prize -> mapPrizeToLaureates(prize) }
    }

    override suspend fun addFavoritePrize(prizeId: Int) {
        apiService.addFavoritePrize(prizeId)
    }

    override suspend fun removeFavoritePrize(prizeId: Int) {
        apiService.removeFavoritePrize(prizeId)
    }

    private fun mapPrizeToLaureates(prize: PrizeDto): List<Laureate> {
        return if (prize.laureates.isEmpty()) {
            listOf(
                Laureate(
                    id = "${prize.id}_0",
                    fullName = prize.fullName,
                    year = prize.awardYear,
                    category = prize.category,
                    motivation = prize.motivation
                )
            )
        } else {
            prize.laureates.map { laureate ->
                Laureate(
                    id = "${prize.id}_${laureate.id}",
                    fullName = laureate.fullName,
                    year = prize.awardYear,
                    category = prize.category,
                    motivation = laureate.motivation
                )
            }
        }
    }
}
