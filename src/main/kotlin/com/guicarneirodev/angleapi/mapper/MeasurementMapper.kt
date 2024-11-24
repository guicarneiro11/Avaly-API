package com.guicarneirodev.angleapi.mapper

import com.guicarneirodev.angleapi.model.domain.Measurement
import com.guicarneirodev.angleapi.model.dto.MeasurementDTO
import com.guicarneirodev.angleapi.util.formatBrazilian
import com.guicarneirodev.angleapi.util.toLocalDateTime

object MeasurementMapper {
    fun toDTO(measurement: Measurement): MeasurementDTO =
        MeasurementDTO(
            name = measurement.name,
            value = measurement.value,
            created = measurement.created.formatBrazilian()
        )

    fun fromDTO(dto: MeasurementDTO): Measurement =
        Measurement(
            name = dto.name,
            value = dto.value,
            created = dto.created.toLocalDateTime()
        )
}