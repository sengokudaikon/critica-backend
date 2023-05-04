package net.critika.infrastructure.validation

interface Validator<T> {
    fun validate(value: T): Boolean
}
