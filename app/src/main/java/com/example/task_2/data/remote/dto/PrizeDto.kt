package com.example.task_2.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrizeDto(
    val id: Int = 0,
    val awardYear: String = "",
    val category: String = "",
    val fullName: String = "",
    val motivation: String = "",
    val detailLink: String? = null,
    val laureates: List<LaureateDtoServer> = emptyList()
)

@Serializable
data class LaureateDtoServer(
    val id: Int = 0,
    val fullName: String = "",
    val portion: String = "",
    val motivation: String = "",
    val portraitUrl: String? = null
)
