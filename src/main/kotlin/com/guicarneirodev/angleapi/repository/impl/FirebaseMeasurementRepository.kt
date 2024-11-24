package com.guicarneirodev.angleapi.repository.impl

import com.google.cloud.firestore.Firestore
import com.guicarneirodev.angleapi.application.exception.RepositoryException
import com.guicarneirodev.angleapi.model.domain.Measurement
import com.guicarneirodev.angleapi.repository.intefaces.IMeasurementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.cloud.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

class FirebaseMeasurementRepository(private val firestore: Firestore) : IMeasurementRepository {

    override suspend fun getMeasurements(userId: String, patientId: String): List<Measurement> =
        withContext(Dispatchers.IO) {
            try {
                getMeasurementsCollection(userId, patientId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        Measurement(
                            name = doc.getString("name") ?: return@mapNotNull null,
                            value = doc.getString("value") ?: return@mapNotNull null,
                            created = doc.getTimestamp("created")?.toLocalDateTime()
                                ?: LocalDateTime.now()
                        )
                    }
            } catch (e: Exception) {
                throw RepositoryException("Error fetching measurements", e)
            }
        }

    override suspend fun addMeasurement(
        userId: String,
        patientId: String,
        measurement: Measurement
    ): Measurement =
        withContext(Dispatchers.IO) {
            try {
                val docRef = getMeasurementsCollection(userId, patientId).document()

                val measurementData = mapOf(
                    "name" to measurement.name,
                    "value" to measurement.value,
                    "created" to Timestamp.now()
                )

                docRef.set(measurementData).await()
                measurement
            } catch (e: Exception) {
                throw RepositoryException("Error adding measurement", e)
            }
        }

    private fun getMeasurementsCollection(userId: String, patientId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("patients")
            .document(patientId)
            .collection("results")

    private suspend fun <T> com.google.api.core.ApiFuture<T>.await(): T =
        withContext(Dispatchers.IO) { get() }

    private fun Timestamp.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(this.toDate().toInstant(), ZoneId.systemDefault())
}