package io.critica.infrastructure.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PlayerNameValidator::class])
annotation class ValidPlayerName(
    val message: String = "Invalid player name format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
