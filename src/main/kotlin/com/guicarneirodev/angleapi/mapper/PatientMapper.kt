package com.guicarneirodev.angleapi.mapper

import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.PatientDTO
import com.guicarneirodev.angleapi.util.formatBrazilian
import com.guicarneirodev.angleapi.util.toLocalDateTime

object PatientMapper {
    fun toDTO(patient: Patient): PatientDTO =
        PatientDTO(
            id = patient.id,
            name = patient.name,
            evaluationDate = patient.evaluationDate.formatBrazilian(),
            measurements = patient.measurements.map { MeasurementMapper.toDTO(it) }
        )

    fun fromDTO(dto: PatientDTO, userId: String): Patient =
        Patient(
            id = dto.id,
            userId = userId,
            name = dto.name,
            evaluationDate = dto.evaluationDate.toLocalDateTime(),
            measurements = dto.measurements.map { MeasurementMapper.fromDTO(it) }
        )
}