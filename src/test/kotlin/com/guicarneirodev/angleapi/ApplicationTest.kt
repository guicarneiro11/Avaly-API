package com.guicarneirodev.angleapi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.guicarneirodev.angleapi.application.plugins.UserPrincipal
import com.guicarneirodev.angleapi.controller.PatientController
import com.guicarneirodev.angleapi.mapper.MeasurementMapper
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.EmailRequestDTO
import com.guicarneirodev.angleapi.model.dto.MeasurementDTO
import com.guicarneirodev.angleapi.model.dto.PatientDTO
import com.guicarneirodev.angleapi.service.interfaces.IPatientService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test

class ApplicationTest {
    private val mockPatientService = mockk<IPatientService>()
    private val testUserId = "test-user-id"
    private val validToken = "valid-token"

    @BeforeTest
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        val mockFirebaseAuth = mockk<FirebaseAuth>()
        val mockFirebaseToken = mockk<FirebaseToken>()

        every { mockFirebaseToken.uid } returns testUserId

        every { FirebaseAuth.getInstance() } returns mockFirebaseAuth
        every {
            mockFirebaseAuth.verifyIdToken(validToken)
        } returns mockFirebaseToken
        every {
            mockFirebaseAuth.verifyIdToken(neq(validToken))
        } throws RuntimeException("Token inválido")
    }

    @Test
    fun testHealthCheck() = testApplication {
        application {
            configureTestModule(mockPatientService)
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = Json.decodeFromString<Map<String, String>>(response.bodyAsText())
        assertEquals("OK", responseBody["status"])
    }

    @Test
    fun testGetAllPatients() = testApplication {
        // Arrange
        val testPatients = listOf(
            Patient(
                id = "1",
                userId = testUserId,
                name = "Test Patient",
                evaluationDate = LocalDateTime.now(),
                measurements = emptyList()
            )
        )

        coEvery {
            mockPatientService.getAllPatients(testUserId)
        } returns testPatients

        application {
            configureTestModule(mockPatientService)
        }

        // Act
        val response = client.get("/api/patients") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val patients = Json.decodeFromString<List<PatientDTO>>(response.bodyAsText())
        assertEquals(1, patients.size)
        assertEquals("Test Patient", patients[0].name)
    }

    @Test
    fun testCreatePatient() = testApplication {
        // Arrange
        val newPatientDTO = PatientDTO(
            id = "",
            name = "New Patient",
            evaluationDate = LocalDateTime.now().toString(),
            measurements = emptyList()
        )

        val createdPatient = Patient(
            id = "new-id",
            userId = testUserId,
            name = newPatientDTO.name,
            evaluationDate = LocalDateTime.now(),
            measurements = emptyList()
        )

        coEvery {
            mockPatientService.createPatient(testUserId, any())
        } returns createdPatient

        application {
            configureTestModule(mockPatientService)
        }

        // Act
        val response = client.post("/api/patients") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newPatientDTO))
        }

        // Assert
        assertEquals(HttpStatusCode.Created, response.status)
        val responsePatient = Json.decodeFromString<PatientDTO>(response.bodyAsText())
        assertEquals("New Patient", responsePatient.name)
    }

    @Test
    fun testAddMeasurement() = testApplication {
        // Arrange
        val patientId = "patient-1"
        val measurementDTO = MeasurementDTO(
            name = "Flexão de Joelho",
            value = "120°",
            created = LocalDateTime.now().toString()
        )

        val measurement = MeasurementMapper.fromDTO(measurementDTO)

        coEvery {
            mockPatientService.addMeasurement(testUserId, patientId, measurement)
        } returns measurement

        application {
            configureTestModule(mockPatientService)
        }

        // Act
        val response = client.post("/api/patients/$patientId/measurements") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(measurementDTO))
        }

        // Assert
        assertEquals(HttpStatusCode.Created, response.status)
        val responseMeasurement = Json.decodeFromString<MeasurementDTO>(response.bodyAsText())
        assertEquals("Flexão de Joelho", responseMeasurement.name)
    }

    @Test
    fun testGenerateAndSendReport() = testApplication {
        // Arrange
        val patientId = "patient-1"
        val emailRequest = EmailRequestDTO(email = "test@example.com")

        coEvery {
            mockPatientService.generateAndSendReport(testUserId, patientId, emailRequest.email)
        } just runs

        application {
            configureTestModule(mockPatientService)
        }

        // Act
        val response = client.post("/api/patients/$patientId/report") {
            header(HttpHeaders.Authorization, "Bearer $validToken")
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(emailRequest))
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testUnauthorizedAccess() = testApplication {
        application {
            configureTestModule(mockPatientService)
        }

        val response = client.get("/api/patients")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testInvalidToken() = testApplication {
        application {
            configureTestModule(mockPatientService)
        }

        val response = client.get("/api/patients") {
            header(HttpHeaders.Authorization, "Bearer invalid_token")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    private fun Application.configureTestModule(
        patientService: IPatientService
    ) {
        install(ContentNegotiation) {
            json()
        }

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

        routing {
            get("/health") {
                call.respond(mapOf("status" to "OK"))
            }

            authenticate("firebase-auth") {
                PatientController(patientService).apply {
                    patientRouting()
                }
            }
        }
    }
}