package com.guicarneirodev.angleapi.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PatientDTO(
    val id: String,
    val name: String,
    val evaluationDate: String,
    val measurements: List<MeasurementDTO>
)