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
import java.time.LocalDateTime
import java.time.ZoneId

class FirebasePatientRepository(
    private val firestore: Firestore,
    private val measurementRepository: IMeasurementRepository
) : IPatientRepository {
    override suspend fun getAllPatientsByUserId(userId: String): List<Patient> =
        withContext(Dispatchers.IO) {
            try {
                getUserPatientsRef(userId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        getPatientData(userId, doc.id)
                    }
            } catch (e: Exception) {
                throw RepositoryException("Error fetching patients", e)
            }
        }

    override suspend fun getPatientData(userId: String, patientId: String): Patient? =
        withContext(Dispatchers.IO) {
            try {
                val patientDoc = getPatientRef(userId, patientId).get().await()
                if (!patientDoc.exists()) return@withContext null

                val data = patientDoc.data ?: return@withContext null
                val measurements = measurementRepository.getMeasurements(userId, patientId)

                Patient(
                    id = patientId,
                    userId = userId,
                    name = data["name"] as? String ?: "",
                    evaluationDate = (data["evaluationDate"] as? Timestamp)?.toLocalDateTime()
                        ?: LocalDateTime.now(),
                    measurements = measurements
                )
            } catch (e: Exception) {
                throw RepositoryException("Error fetching patient", e)
            }
        }

    override suspend fun createPatient(userId: String, patient: Patient): Patient =
        withContext(Dispatchers.IO) {
            try {
                val docRef = getUserPatientsRef(userId).document()
                val patientData = mapOf(
                    "name" to patient.name,
                    "evaluationDate" to Timestamp.now()
                )

                docRef.set(patientData).await()
                getPatientData(userId, docRef.id)
                    ?: throw RepositoryException("Failed to create patient")
            } catch (e: Exception) {
                throw RepositoryException("Error creating patient", e)
            }
        }

    override suspend fun updatePatient(userId: String, patient: Patient): Patient =
        withContext(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "name" to patient.name,
                    "evaluationDate" to Timestamp.now()
                )

                getPatientRef(userId, patient.id).update(updates).await()
                getPatientData(userId, patient.id)
                    ?: throw NotFoundException("Patient not found after update")
            } catch (e: Exception) {
                throw RepositoryException("Error updating patient", e)
            }
        }

    override suspend fun deletePatient(userId: String, patientId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                getPatientRef(userId, patientId).delete().await()
                true
            } catch (e: Exception) {
                false
            }
        }

    private fun getUserPatientsRef(userId: String) =
        firestore.collection("users").document(userId).collection("patients")

    private fun getPatientRef(userId: String, patientId: String) =
        getUserPatientsRef(userId).document(patientId)

    private fun Timestamp.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(toDate().toInstant(), ZoneId.systemDefault())

    private suspend fun <T> com.google.api.core.ApiFuture<T>.await(): T =
        withContext(Dispatchers.IO) { get() }
}