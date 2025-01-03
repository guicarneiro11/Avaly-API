package com.guicarneirodev.angleapi.application.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.guicarneirodev.angleapi.application.exception.UnauthorizedException
import io.ktor.server.plugins.*

class UserPrincipal(
    val userId: String
) : Principal

fun Application.configureSecurity() {
    authentication {
        bearer("firebase-auth") {
            authenticate { credentials ->
                try {
                    val token = credentials.token ?: throw BadRequestException("No token provided")

                    if (!token.startsWith("Bearer ", ignoreCase = true)) {
                        throw BadRequestException("Token must start with 'Bearer'")
                    }

                    val actualToken = token.substring(7)
                    if (actualToken.isBlank()) {
                        throw BadRequestException("Token cannot be empty")
                    }

                    val decodedToken = FirebaseAuth.getInstance()
                        .verifyIdToken(actualToken)
                        ?: throw UnauthorizedException("Invalid token")

                    UserPrincipal(decodedToken.uid)
                } catch (e: FirebaseAuthException) {
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}