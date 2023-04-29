package io.critica.infrastructure.validation

class EmailValidator : Validator<String> {
    override fun validate(value: String): Boolean {
        val emailPattern = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        val emailRegex = Regex(emailPattern)
        return emailRegex.matches(value)
    }
}

