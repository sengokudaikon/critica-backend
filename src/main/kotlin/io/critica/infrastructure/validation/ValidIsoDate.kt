package io.critica.infrastructure.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [IsoDateValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ValidIsoDate(
    val message: String = "Invalid ISO date format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
