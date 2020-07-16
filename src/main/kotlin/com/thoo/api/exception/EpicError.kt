package com.thoo.api.exception

class EpicError(
    val errorCode: String,
    val errorMessage: String,
    val messageVars: Array<String>,
    val numericErrorCode: Int?,
    val originatingService: String,
    val intent: String
)