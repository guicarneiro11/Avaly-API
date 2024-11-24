package com.guicarneirodev.angleapi.util

import com.guicarneirodev.angleapi.mapper.MeasurementMapper
import com.guicarneirodev.angleapi.model.domain.Measurement
import com.guicarneirodev.angleapi.model.dto.MeasurementDTO

fun Measurement.toMeasurementDTO(): MeasurementDTO = MeasurementMapper.toDTO(this)
fun MeasurementDTO.toMeasurement(): Measurement = MeasurementMapper.fromDTO(this)
fun List<Measurement>.toMeasurementDTOs(): List<MeasurementDTO> = map { it.toMeasurementDTO() }
fun List<MeasurementDTO>.toMeasurements(): List<Measurement> = map { it.toMeasurement() }