package io.critica.infrastructure.validation

import kotlin.reflect.full.declaredMemberProperties

interface Validator<T> {
    fun validate(value: T): Boolean
}
