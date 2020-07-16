package com.thoo.api.model

data class Exchange(
    val code: String,
    val creatingClientId: String,
    val expiresInSeconds: Int
)