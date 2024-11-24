package com.guicarneirodev.angleapi.controller

import com.guicarneirodev.angleapi.application.exception.UnauthorizedException
import com.guicarneirodev.angleapi.application.plugins.UserPrincipal
import com.guicarneirodev.angleapi.mapper.MeasurementMapper
import com.guicarneirodev.angleapi.mapper.PatientMapper
import com.guicarneirodev.angleapi.model.dto.EmailRequestDTO
import com.guicarneirodev.angleapi.model.dto.MeasurementDTO
import com.guicarneirodev.angleapi.model.dto.PatientDTO
import com.guicarneirodev.angleapi.service.interfaces.IPatientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

class PatientController(private val patientService: IPatientService) {
    fun Route.patientRouting() {
        route("/api/patients") {
            // Listar todos os pacientes
            get {
                val patients = patientService.getAllPatients(getCurrentUserId())
                call.respond(patients.map { PatientMapper.toDTO(it) })
            }

            // Obter paciente específico
            get("{id}") {
                val patientId = getRequiredParam("id")
                val patient = patientService.getPatient(getCurrentUserId(), patientId)
                call.respond(PatientMapper.toDTO(patient))
            }

            // Criar novo paciente
            post {
                val patientDTO = call.receive<PatientDTO>()
                val patient = patientService.createPatient(
                    getCurrentUserId(),
                    patientDTO
                )
                call.respond(HttpStatusCode.Created, PatientMapper.toDTO(patient))
            }

            // Atualizar paciente
            put("{id}") {
                val patientId = getRequiredParam("id")
                val patientDTO = call.receive<PatientDTO>()
                val updated = patientService.updatePatient(
                    getCurrentUserId(),
                    patientId,
                    patientDTO
                )
                call.respond(PatientMapper.toDTO(updated))
            }

            // Deletar paciente
            delete("{id}") {
                val patientId = getRequiredParam("id")
                patientService.deletePatient(getCurrentUserId(), patientId)
                call.respond(HttpStatusCode.NoContent)
            }

            // Gerar e enviar relatório
            post("{id}/report") {
                val patientId = getRequiredParam("id")
                val request = call.receive<EmailRequestDTO>()

                patientService.generateAndSendReport(
                    getCurrentUserId(),
                    patientId,
                    request.email
                )

                call.respond(HttpStatusCode.OK, mapOf("message" to "Report sent successfully"))
            }

            // Adicionar medição
            post("{id}/measurements") {
                val patientId = getRequiredParam("id")
                val measurementDTO = call.receive<MeasurementDTO>()

                val measurement = patientService.addMeasurement(
                    getCurrentUserId(),
                    patientId,
                    MeasurementMapper.fromDTO(measurementDTO)
                )

                call.respond(HttpStatusCode.Created, MeasurementMapper.toDTO(measurement))
            }
        }
    }

    private fun PipelineContext<Unit, ApplicationCall>.getRequiredParam(name: String): String {
        return call.parameters[name] ?: throw BadRequestException("$name is required")
    }

    private fun PipelineContext<Unit, ApplicationCall>.getCurrentUserId(): String {
        return call.principal<UserPrincipal>()?.userId
            ?: throw UnauthorizedException("User not authenticated")
    }
}