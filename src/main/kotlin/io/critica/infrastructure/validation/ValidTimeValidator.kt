package io.critica.infrastructure.validation

import java.time.LocalTime
import java.time.format.DateTimeParseException
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class ValidTimeValidator : ConstraintValidator<ValidTime, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true

        return try {
            LocalTime.parse(value)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    override fun initialize(constraintAnnotation: ValidTime) {}
}
