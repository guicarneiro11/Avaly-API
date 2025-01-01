package com.guicarneirodev.angleapi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import com.guicarneirodev.angleapi.application.exception.configureExceptionHandling
import com.guicarneirodev.angleapi.application.plugins.UserPrincipal
import com.guicarneirodev.angleapi.application.plugins.configureHTTP
import com.guicarneirodev.angleapi.application.plugins.configureSerialization
import com.guicarneirodev.angleapi.config.EmailConfig
import com.guicarneirodev.angleapi.config.FirebaseConfig
import com.guicarneirodev.angleapi.controller.PatientController
import com.guicarneirodev.angleapi.repository.impl.FirebaseMeasurementRepository
import com.guicarneirodev.angleapi.repository.impl.FirebasePatientRepository
import com.guicarneirodev.angleapi.service.impl.EmailService
import com.guicarneirodev.angleapi.service.impl.PatientService
import com.guicarneirodev.angleapi.util.PdfGenerator
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    try {
        println("Starting application initialization...")

        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
        println("Configured port: $port")

        embeddedServer(
            Netty,
            port = port,
            host = "0.0.0.0",
            module = { module() }
        ).apply {
            println("Server configured, starting...")
            start(wait = true)
        }
    } catch (e: Exception) {
        println("Critical error starting server: ${e.message}")
        e.printStackTrace()
        throw e
    }
}

fun Application.module() {
    FirebaseConfig.initialize()

    install(Authentication) {
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

    configureSerialization()
    configureHTTP()
    configureExceptionHandling()

    val firestore = FirestoreClient.getFirestore()
    val measurementRepository = FirebaseMeasurementRepository(firestore)
    val patientRepository = FirebasePatientRepository(firestore, measurementRepository)
    val emailService = EmailService(EmailConfig())
    val pdfGenerator = PdfGenerator()

    val patientService = PatientService(
        patientRepository = patientRepository,
        measurementRepository = measurementRepository,
        emailService = emailService,
        pdfGenerator = pdfGenerator
    )

    val patientController = PatientController(patientService)

    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        authenticate("firebase-auth") {
            patientController.apply { patientRouting() }
        }
    }
}