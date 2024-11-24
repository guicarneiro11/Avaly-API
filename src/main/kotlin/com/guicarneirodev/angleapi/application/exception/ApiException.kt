package com.guicarneirodev.angleapi.application.exception

sealed class ApiException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

class BadRequestException(message: String) : ApiException(message)
class UnauthorizedException(message: String) : ApiException(message)
class EmailException(message: String, cause: Throwable? = null) : ApiException(message, cause)