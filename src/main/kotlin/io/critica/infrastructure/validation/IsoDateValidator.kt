package io.critica.infrastructure.validation

import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class IsoDateValidator : ConstraintValidator<ValidIsoDate, String> {
    override fun initialize(constraintAnnotation: ValidIsoDate?) {}

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true

        return try {
            val date = LocalDate.parse(value)
            !date.isBefore(LocalDate.now())
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
