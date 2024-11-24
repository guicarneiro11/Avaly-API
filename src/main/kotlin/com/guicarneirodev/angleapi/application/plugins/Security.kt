package com.guicarneirodev.angleapi.application.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import com.google.firebase.auth.FirebaseAuth

class UserPrincipal(
    val userId: String
) : Principal

fun Application.configureSecurity() {
    authentication {
        bearer("firebase-auth") {
            authenticate { tokenCredential ->
                try {
                    val decodedToken = FirebaseAuth.getInstance()
                        .verifyIdToken(tokenCredential.token)
                    UserPrincipal(decodedToken.uid)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}