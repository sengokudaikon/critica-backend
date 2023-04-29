package io.critica.infrastructure.validation

import java.util.*

class UUIDValidator {
    fun validate(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (ex: IllegalArgumentException) {
            false
        }
    }
}
