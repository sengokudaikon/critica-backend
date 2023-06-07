package net.critika.infrastructure.validation

import kotlinx.uuid.UUID

class UUIDValidator {
    @Suppress("SwallowedException")
    fun validate(uuid: String): Boolean {
        return try {
            UUID(uuid)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }
}
