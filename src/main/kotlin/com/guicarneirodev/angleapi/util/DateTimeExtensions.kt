package com.guicarneirodev.angleapi.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatBrazilian(): String =
    this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)