package com.guicarneirodev.angleapi.config

data class EmailConfig(
    val host: String = "smtp.gmail.com",
    val port: Int = 587,
    val username: String = "EMAIL",
    val password: String = "SENHA",
    val fromEmail: String = "EMAIL"
)
