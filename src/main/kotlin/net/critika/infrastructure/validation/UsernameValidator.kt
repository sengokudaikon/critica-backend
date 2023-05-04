package net.critika.infrastructure.validation

class UsernameValidator : Validator<String> {
    override fun validate(value: String): Boolean {
        val usernamePattern = "^[A-Za-z0-9]*\$"
        val usernameRegex = Regex(usernamePattern)
        return usernameRegex.matches(value)
    }
}
