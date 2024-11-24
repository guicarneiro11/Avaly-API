package com.guicarneirodev.angleapi.application.plugins

import com.guicarneirodev.angleapi.controller.PatientController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(patientController: PatientController) {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, mapOf("status" to "OK"))
        }

        authenticate("firebase-auth") {
            with(patientController) {
                patientRouting()
            }
        }
    }
}