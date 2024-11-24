package com.guicarneirodev.angleapi.model.domain

import java.time.LocalDateTime

data class Measurement(
    val name: String,
    val value: String,
    val created: LocalDateTime
)