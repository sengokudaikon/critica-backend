package io.critica.infrastructure.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [ValidTimeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidTime(
    val message: String = "Invalid time format, must be HH:mm",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
