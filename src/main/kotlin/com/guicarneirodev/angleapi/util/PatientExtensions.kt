package com.guicarneirodev.angleapi.util

import com.guicarneirodev.angleapi.mapper.PatientMapper
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.PatientDTO

fun Patient.toPatientDTO(): PatientDTO = PatientMapper.toDTO(this)
fun PatientDTO.toPatient(userId: String): Patient = PatientMapper.fromDTO(this, userId)