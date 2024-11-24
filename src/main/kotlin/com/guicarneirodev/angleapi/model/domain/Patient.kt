package com.guicarneirodev.angleapi.model.domain

import java.time.LocalDateTime

data class Patient(
    val id: String,
    val userId: String,
    val name: String,
    val evaluationDate: LocalDateTime,
    val measurements: List<Measurement>
)