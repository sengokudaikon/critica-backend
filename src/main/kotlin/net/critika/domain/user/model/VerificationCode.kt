package net.critika.domain.user.model

import kotlinx.uuid.UUID
import kotlinx.uuid.exposed.KotlinxUUIDEntity
import kotlinx.uuid.exposed.KotlinxUUIDEntityClass
import net.critika.persistence.user.entity.UserVerificationCodes
import org.jetbrains.exposed.dao.id.EntityID

class VerificationCode(id: EntityID<UUID>) : KotlinxUUIDEntity(id) {
    companion object : KotlinxUUIDEntityClass<VerificationCode>(UserVerificationCodes)

    var userId by User referencedOn UserVerificationCodes.userId
    var code by UserVerificationCodes.code
    var expiresAt by UserVerificationCodes.expiresAt
}
