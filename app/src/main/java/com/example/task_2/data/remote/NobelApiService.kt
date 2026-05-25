package com.example.task_2.data.remote

import com.example.task_2.data.remote.dto.LoginRequestDto
import com.example.task_2.data.remote.dto.PrizeDto
import com.example.task_2.data.remote.dto.TokenResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

private const val BASE_URL = "http://10.0.2.2:8080"

class NobelApiService(private val client: HttpClient) {

    suspend fun getPrizes(): List<PrizeDto> {
        return client.get("$BASE_URL/prizes").body()
    }

    suspend fun getPrize(id: Int): PrizeDto {
        return client.get("$BASE_URL/prizes/$id").body()
    }

    suspend fun login(username: String, password: String): TokenResponseDto {
        return client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }.body()
    }

    suspend fun getFavoritePrizes(): List<PrizeDto> {
        return client.get("$BASE_URL/users/me/prizes") {
            header(HttpHeaders.Authorization, "Bearer ${TokenStorage.token}")
        }.body()
    }

    suspend fun addFavoritePrize(prizeId: Int) {
        client.post("$BASE_URL/users/me/prizes/$prizeId") {
            header(HttpHeaders.Authorization, "Bearer ${TokenStorage.token}")
        }
    }

    suspend fun removeFavoritePrize(prizeId: Int) {
        client.delete("$BASE_URL/users/me/prizes/$prizeId") {
            header(HttpHeaders.Authorization, "Bearer ${TokenStorage.token}")
        }
    }
}
