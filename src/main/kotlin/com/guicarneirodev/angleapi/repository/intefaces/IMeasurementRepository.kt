package com.guicarneirodev.angleapi.repository.intefaces

import com.guicarneirodev.angleapi.model.domain.Measurement

interface IMeasurementRepository {
    suspend fun getMeasurements(userId: String, patientId: String): List<Measurement>
    suspend fun addMeasurement(userId: String, patientId: String, measurement: Measurement): Measurement
}