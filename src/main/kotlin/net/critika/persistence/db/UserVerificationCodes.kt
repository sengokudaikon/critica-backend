package net.critika.persistence.db

import org.jetbrains.exposed.dao.id.UUIDTable
import org.joda.time.DateTime

object UserVerificationCodes : UUIDTable("user_verification_codes") {
    val userId = reference("user_id", Users)
    val code = varchar("code", 6)
    val expiresAt = long("expires_at").default(DateTime.now().plusHours(8).millis)
}