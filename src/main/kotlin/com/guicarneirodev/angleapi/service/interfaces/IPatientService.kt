package com.guicarneirodev.angleapi.service.interfaces

import com.guicarneirodev.angleapi.model.domain.Measurement
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.PatientDTO

interface IPatientService {
    suspend fun getPatient(userId: String, patientId: String): Patient
    suspend fun getAllPatients(userId: String): List<Patient>
    suspend fun createPatient(userId: String, patientDTO: PatientDTO): Patient
    suspend fun updatePatient(userId: String, patientId: String, patientDTO: PatientDTO): Patient
    suspend fun deletePatient(userId: String, patientId: String)
    suspend fun generateAndSendReport(userId: String, patientId: String, email: String)
    suspend fun addMeasurement(userId: String, patientId: String, measurement: Measurement): Measurement
}
