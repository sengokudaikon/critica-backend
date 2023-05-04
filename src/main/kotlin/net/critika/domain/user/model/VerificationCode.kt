package net.critika.domain.user.model

import net.critika.persistence.db.UserVerificationCodes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class VerificationCode(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<VerificationCode>(UserVerificationCodes)

    var userId by User referencedOn UserVerificationCodes.userId
    var code by UserVerificationCodes.code
    var expiresAt by UserVerificationCodes.expiresAt
}