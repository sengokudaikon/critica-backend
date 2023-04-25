package io.critica.infrastructure.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UUIDValidator::class])
annotation class ValidUUID(
    val message: String = "Invalid UUID format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
