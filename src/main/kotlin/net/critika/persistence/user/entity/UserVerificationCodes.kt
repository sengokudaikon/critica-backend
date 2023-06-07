package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import org.joda.time.DateTime

object UserVerificationCodes : KotlinxUUIDTable("user_verification_codes") {
    val userId = reference("user_id", Users)
    val code = varchar("code", 6)
    val expiresAt = long("expires_at").default(DateTime.now().plusHours(8).millis)
}