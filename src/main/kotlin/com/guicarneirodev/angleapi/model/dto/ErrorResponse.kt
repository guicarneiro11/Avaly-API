package com.guicarneirodev.angleapi.model.dto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val path: String,
    val timestamp: String = LocalDateTime.now().toString()
)