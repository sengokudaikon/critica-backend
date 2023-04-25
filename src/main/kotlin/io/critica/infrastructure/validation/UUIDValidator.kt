package io.critica.infrastructure.validation

import java.util.UUID
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class UUIDValidator : ConstraintValidator<ValidUUID, String> {
    override fun initialize(constraintAnnotation: ValidUUID?) {}

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        return try {
            UUID.fromString(value)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
