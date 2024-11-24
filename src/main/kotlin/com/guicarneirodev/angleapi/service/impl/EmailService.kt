package com.guicarneirodev.angleapi.service.impl

import com.guicarneirodev.angleapi.application.exception.EmailException
import com.guicarneirodev.angleapi.config.EmailConfig
import com.guicarneirodev.angleapi.service.interfaces.IEmailService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.guicarneirodev.angleapi.service.interfaces.EmailAttachment
import org.apache.commons.mail.MultiPartEmail
import javax.mail.util.ByteArrayDataSource

class EmailService(private val config: EmailConfig) : IEmailService {

    override suspend fun sendEmail(
        to: String,
        subject: String,
        content: String,
        attachment: EmailAttachment?
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val email = MultiPartEmail().apply {
                hostName = config.host
                setSmtpPort(config.port)
                isStartTLSEnabled = true
                setAuthentication(config.username, config.password)
                setFrom(config.fromEmail)
                addTo(to)
                setSubject(subject)
                setMsg(content)
            }

            attachment?.let {
                val dataSource = ByteArrayDataSource(it.content, it.contentType)
                email.attach(dataSource, it.name, "")
            }

            email.send()
        } catch (e: Exception) {
            throw EmailException("Failed to send email", e)
        }
    }
}