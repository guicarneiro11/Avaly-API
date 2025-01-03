package com.guicarneirodev.angleapi.repository.impl

import com.google.cloud.Timestamp
import com.google.cloud.firestore.Firestore
import com.guicarneirodev.angleapi.application.exception.NotFoundException
import com.guicarneirodev.angleapi.application.exception.RepositoryException
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.repository.intefaces.IMeasurementRepository
import com.guicarneirodev.angleapi.repository.intefaces.IPatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId

class FirebasePatientRepository(
    private val firestore: Firestore, private val measurementRepository: IMeasurementRepository
) : IPatientRepository {
    private val logger = LoggerFactory.getLogger(FirebasePatientRepository::class.java)

    private fun getUserPatientsRef(userId: String) =
        firestore.collection("users").document(userId).collection("patients")

    private fun getPatientRef(userId: String, patientId: String) = getUserPatientsRef(userId).document(patientId)

    private fun Timestamp.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(toDate().toInstant(), ZoneId.systemDefault())

    private suspend fun <T> com.google.api.core.ApiFuture<T>.await(): T = withContext(Dispatchers.IO) { get() }

    override suspend fun getAllPatientsByUserId(userId: String): List<Patient> = withContext(Dispatchers.IO) {
        try {
            logger.info("Fetching patients for user: $userId")
            val patientsRef = getUserPatientsRef(userId)
            val snapshot = patientsRef.get().await()

            logger.info("Got ${snapshot.documents.size} patients")

            snapshot.documents.mapNotNull { doc ->
                try {
                    getPatientData(userId, doc.id)
                } catch (e: Exception) {
                    logger.error("Error fetching patient ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            logger.error("Error fetching patients: ${e.message}", e)
            throw RepositoryException("Error fetching patients: ${e.message}", e)
        }
    }

    override suspend fun getPatientData(userId: String, patientId: String): Patient? =
        withContext(Dispatchers.IO) {
            try {
                val patientDoc = getPatientRef(userId, patientId).get().await()
                if (!patientDoc.exists()) return@withContext null

                val data = patientDoc.data ?: return@withContext null
                val measurements = measurementRepository.getMeasurements(userId, patientId)

                logger.info("Patient data: $data") // Adicione log

                Patient(
                    id = patientId,
                    userId = userId,
                    name = data["patientName"] as? String ?: "",
                    evaluationDate = (data["evaluationDate"] as? Timestamp)?.toLocalDateTime()
                        ?: LocalDateTime.now(),
                    measurements = measurements
                )
            } catch (e: Exception) {
                logger.error("Error fetching patient: ${e.message}", e)
                null
            }
        }

    override suspend fun createPatient(userId: String, patient: Patient): Patient = withContext(Dispatchers.IO) {
        try {
            val docRef = getUserPatientsRef(userId).document()
            val patientData = mapOf(
                "patientName" to patient.name, "evaluationDate" to Timestamp.now()
            )

            docRef.set(patientData).await()
            getPatientData(userId, docRef.id) ?: throw RepositoryException("Failed to create patient")
        } catch (e: Exception) {
            throw RepositoryException("Error creating patient", e)
        }
    }

    override suspend fun updatePatient(userId: String, patient: Patient): Patient = withContext(Dispatchers.IO) {
        try {
            val updates = mapOf(
                "patientName" to patient.name, "evaluationDate" to Timestamp.now()
            )

            getPatientRef(userId, patient.id).update(updates).await()
            getPatientData(userId, patient.id) ?: throw NotFoundException("Patient not found after update")
        } catch (e: Exception) {
            throw RepositoryException("Error updating patient", e)
        }
    }

    override suspend fun deletePatient(userId: String, patientId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            getPatientRef(userId, patientId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}