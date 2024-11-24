package com.guicarneirodev.angleapi.service.impl

import com.guicarneirodev.angleapi.application.exception.BadRequestException
import com.guicarneirodev.angleapi.application.exception.BusinessException
import com.guicarneirodev.angleapi.application.exception.NotFoundException
import com.guicarneirodev.angleapi.mapper.PatientMapper
import com.guicarneirodev.angleapi.model.domain.Measurement
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.PatientDTO
import com.guicarneirodev.angleapi.repository.intefaces.IMeasurementRepository
import com.guicarneirodev.angleapi.repository.intefaces.IPatientRepository
import com.guicarneirodev.angleapi.service.interfaces.EmailAttachment
import com.guicarneirodev.angleapi.service.interfaces.IEmailService
import com.guicarneirodev.angleapi.service.interfaces.IPatientService
import com.guicarneirodev.angleapi.util.PdfGenerator
import com.guicarneirodev.angleapi.util.toLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class PatientService(
    private val patientRepository: IPatientRepository,
    private val measurementRepository: IMeasurementRepository,
    private val emailService: IEmailService,
    private val pdfGenerator: PdfGenerator
) : IPatientService {
    private val logger = LoggerFactory.getLogger(PatientService::class.java)

    override suspend fun getPatient(userId: String, patientId: String): Patient {
        logger.info("Getting patient data - userId: $userId, patientId: $patientId")
        return patientRepository.getPatientData(userId, patientId)
            ?: throw NotFoundException("Patient not found with id: $patientId")
    }

    override suspend fun getAllPatients(userId: String): List<Patient> {
        logger.info("Getting all patients for user: $userId")
        return patientRepository.getAllPatientsByUserId(userId)
    }

    override suspend fun createPatient(userId: String, patientDTO: PatientDTO): Patient {
        logger.info("Creating new patient - userId: $userId")
        try {
            patientDTO.evaluationDate.toLocalDateTime()
        } catch (e: Exception) {
            throw BadRequestException("Data de avaliação inválida. Use o formato: 2024-02-14T15:30:00")
        }

        return patientRepository.createPatient(userId, PatientMapper.fromDTO(patientDTO, userId))
    }

    override suspend fun updatePatient(
        userId: String,
        patientId: String,
        patientDTO: PatientDTO
    ): Patient {
        logger.info("Updating patient - userId: $userId, patientId: $patientId")

        val existingPatient = getPatient(userId, patientId)
        val updatedPatient = existingPatient.copy(
            name = patientDTO.name,
            evaluationDate = patientDTO.evaluationDate.toLocalDateTime()
        )

        return patientRepository.updatePatient(userId, updatedPatient)
    }

    override suspend fun deletePatient(userId: String, patientId: String) {
        logger.info("Deleting patient - userId: $userId, patientId: $patientId")
        if (!patientRepository.deletePatient(userId, patientId)) {
            throw NotFoundException("Patient not found with id: $patientId")
        }
        logger.info("Patient deleted successfully")
    }

    override suspend fun generateAndSendReport(
        userId: String,
        patientId: String,
        email: String
    ) = withContext(Dispatchers.IO) {
        logger.info("Generating and sending report - userId: $userId, patientId: $patientId, email: $email")

        try {
            val patient = getPatient(userId, patientId)
            val pdfContent = pdfGenerator.generatePdf(patient)

            emailService.sendEmail(
                to = email,
                subject = "Relatório do Paciente ${patient.name}",
                content = """
                   Olá,
                   
                   Segue em anexo o relatório do paciente ${patient.name}.
                   
                   Atenciosamente,
                   Equipe AnglePro
               """.trimIndent(),
                attachment = EmailAttachment(
                    name = "relatorio_${patient.name.lowercase().replace(" ", "_")}.pdf",
                    content = pdfContent,
                    contentType = "application/pdf"
                )
            )

            logger.info("Report generated and sent successfully")
        } catch (e: Exception) {
            logger.error("Error generating/sending report", e)
            throw BusinessException("Failed to generate or send report: ${e.message}")
        }
    }

    override suspend fun addMeasurement(
        userId: String,
        patientId: String,
        measurement: Measurement
    ): Measurement {
        logger.info("Adding measurement - userId: $userId, patientId: $patientId")
        getPatient(userId, patientId)
        return measurementRepository.addMeasurement(userId, patientId, measurement)
    }
}