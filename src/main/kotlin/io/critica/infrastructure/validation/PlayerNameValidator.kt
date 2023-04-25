package io.critica.infrastructure.validation

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PlayerNameValidator : ConstraintValidator<ValidPlayerName, String> {
    private val regex = "^[A-Za-z0-9 ]+$".toRegex()

    override fun initialize(constraintAnnotation: ValidPlayerName?) {}

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value?.matches(regex) ?: true
    }
}
