package com.guicarneirodev.angleapi.repository.intefaces

import com.guicarneirodev.angleapi.model.domain.Patient

interface IPatientRepository {
    suspend fun getAllPatientsByUserId(userId: String): List<Patient>
    suspend fun getPatientData(userId: String, patientId: String): Patient?
    suspend fun createPatient(userId: String, patient: Patient): Patient
    suspend fun updatePatient(userId: String, patient: Patient): Patient
    suspend fun deletePatient(userId: String, patientId: String): Boolean
}