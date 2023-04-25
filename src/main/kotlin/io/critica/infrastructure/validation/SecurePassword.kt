package io.critica.infrastructure.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

// Password validation annotation
@Constraint(validatedBy = [SecurePasswordValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SecurePassword(
    val message: String = "Password must be secure and contain at least 8 symbols, digits, or allowed symbols",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
