package net.critika.infrastructure

import io.github.cdimascio.dotenv.Dotenv
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.*

object EmailSender {
    private val dotenv = Dotenv.load()
    private val EMAIL_USERNAME = dotenv["EMAIL_USERNAME"]
    private val EMAIL_PASSWORD = dotenv["EMAIL_PASSWORD"]

    private val properties: Properties by lazy {
        Properties().apply {
            put("mail.smtp.user", "true")
            put("mail.smtp.ssl.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "465")
        }
    }

    private val session: Session by lazy {
        Session.getInstance(
            properties,
            object : jakarta.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD)
                }
            },
        )
    }

    @Throws(MessagingException::class)
    fun sendVerificationCode(to: String, code: String) {
        sendEmail(
            to,
            "Verify your email for Critika",
            code,
        )
    }

    @Throws(MessagingException::class)
    fun sendEmail(to: String, subject: String, body: String) {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(EMAIL_USERNAME))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            setSubject(subject)
            setText(body)
        }

        Transport.send(message)
    }
}
