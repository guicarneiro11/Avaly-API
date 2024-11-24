package com.guicarneirodev.angleapi.util

import com.guicarneirodev.angleapi.mapper.PatientMapper
import com.guicarneirodev.angleapi.model.domain.Patient
import com.guicarneirodev.angleapi.model.dto.PatientDTO

fun Patient.toDTO(): PatientDTO = PatientMapper.toDTO(this)
fun PatientDTO.toEntity(userId: String): Patient = PatientMapper.fromDTO(this, userId)