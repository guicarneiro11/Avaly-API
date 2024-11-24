package com.guicarneirodev.angleapi.application.exception

open class BusinessException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)