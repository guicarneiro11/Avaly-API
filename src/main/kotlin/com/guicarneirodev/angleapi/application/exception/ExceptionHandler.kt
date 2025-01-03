package com.guicarneirodev.angleapi.application.exception

import com.google.firebase.auth.FirebaseAuthException
import com.guicarneirodev.angleapi.model.dto.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound, ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    message = cause.message ?: "Resource not found",
                    path = call.request.path()
                )
            )
        }
        exception<BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest, ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = cause.message ?: "Bad request",
                    path = call.request.path()
                )
            )
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized, ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    message = cause.message ?: "Unauthorized",
                    path = call.request.path()
                )
            )
        }
        exception<EmailException> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }
        exception<Exception> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "Internal Server Error"))
            )
        }
        exception<FirebaseAuthException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    message = "Invalid Firebase token: ${cause.message}",
                    path = call.request.path()
                )
            )
        }
    }
}