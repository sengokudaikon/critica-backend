package io.critica.infrastructure.validation

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.koin.core.annotation.Single

@Single
class Validator {
    fun init(): Validator {
        return Validation.buildDefaultValidatorFactory().validator
    }
}