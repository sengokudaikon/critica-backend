package io.critica.infrastructure.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

// Password validator
class SecurePasswordValidator : ConstraintValidator<SecurePassword, String> {
    override fun initialize(constraintAnnotation: SecurePassword) {}
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false

        // At least 8 symbols, contain digits, or allowed symbols
        val regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,}\$".toRegex()
        return regex.matches(value)
    }
}
