package com.example.task_2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizesResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String = "",
    val category: LocalizedStringDto? = null,
    val laureates: List<LaureateShortDto> = emptyList()
)

@Serializable
data class LocalizedStringDto(
    val en: String = ""
)

@Serializable
data class LaureateShortDto(
    val id: String = "",
    val fullName: LocalizedStringDto? = null,
    val knownName: LocalizedStringDto? = null,
    val orgName: LocalizedStringDto? = null,
    val motivation: LocalizedStringDto? = null
)
