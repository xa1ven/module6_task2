package com.example.task_2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaureateDetailDto(
    val id: String = "",
    val fullName: LocalizedStringDto? = null,
    val knownName: LocalizedStringDto? = null,
    val orgName: LocalizedStringDto? = null,
    val birth: BirthDto? = null,
    val nobelPrizes: List<LaureateNobelPrizeDto> = emptyList()
)

@Serializable
data class BirthDto(
    val place: PlaceDto? = null
)

@Serializable
data class PlaceDto(
    val country: LocalizedStringDto? = null,
    val locationString: LocalizedStringDto? = null
)

@Serializable
data class LaureateNobelPrizeDto(
    val awardYear: String = "",
    val category: LocalizedStringDto? = null,
    val motivation: LocalizedStringDto? = null
)
