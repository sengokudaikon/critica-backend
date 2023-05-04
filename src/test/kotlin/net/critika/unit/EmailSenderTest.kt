package net.critika.unit

import net.critika.infrastructure.EmailSender
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.mail.MessagingException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailSenderTest {

    @Test
    @Suppress("PrintStackTrace")
    fun sendTestEmail() {
        val to = "cryogen.sxu@gmail.com"
        val subject = "Test email from JUnit"
        val body = "This is a test email sent from a JUnit test in Kotlin."

        try {
            EmailSender.sendEmail(to, subject, body)
            println("Test email sent successfully.")
        } catch (e: MessagingException) {
            e.printStackTrace()
            throw e
        }
    }
}
