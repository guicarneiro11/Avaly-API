package com.guicarneirodev.angleapi.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmailRequestDTO(
    val email: String
)