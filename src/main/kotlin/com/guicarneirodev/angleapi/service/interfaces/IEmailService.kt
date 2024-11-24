package com.guicarneirodev.angleapi.service.interfaces

interface IEmailService {
    suspend fun sendEmail(
        to: String,
        subject: String,
        content: String,
        attachment: EmailAttachment? = null
    )
}

class EmailAttachment(
    val name: String,
    val content: ByteArray,
    val contentType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmailAttachment) return false

        if (name != other.name) return false
        if (!content.contentEquals(other.content)) return false
        return contentType == other.contentType
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}