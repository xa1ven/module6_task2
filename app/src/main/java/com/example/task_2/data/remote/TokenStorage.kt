package com.example.task_2.data.remote

object TokenStorage {
    var token: String = ""

    fun isLoggedIn(): Boolean = token.isNotEmpty()
}
