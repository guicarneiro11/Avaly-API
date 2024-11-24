package com.guicarneirodev.angleapi.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementDTO(
    val name: String,
    val value: String,
    val created: String
)