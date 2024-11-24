package com.guicarneirodev.angleapi.util

import com.guicarneirodev.angleapi.model.domain.Patient
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream

class PdfGenerator {
    fun generatePdf(patient: Patient): ByteArray {
        return ByteArrayOutputStream().use { outputStream ->
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            document.add(Paragraph("Relatório do Paciente"))
            document.add(Paragraph("Nome: ${patient.name}"))
            document.add(Paragraph("Data de Avaliação: ${patient.evaluationDate.formatBrazilian()}"))

            document.add(Paragraph("Medições:"))
            patient.measurements.forEach { measurement ->
                document.add(Paragraph("  ${measurement.name}: ${measurement.value}"))
                document.add(Paragraph("  Data: ${measurement.created.formatBrazilian()}"))
                document.add(Paragraph(""))
            }

            document.close()
            outputStream.toByteArray()
        }
    }
}