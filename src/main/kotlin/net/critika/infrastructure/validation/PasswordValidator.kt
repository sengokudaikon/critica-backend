package net.critika.infrastructure.validation
class PasswordValidator : Validator<String> {
    override fun validate(value: String): Boolean {
        val passwordPattern = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`]+\$"
        val passwordRegex = Regex(passwordPattern)
        return passwordRegex.matches(value)
    }
}
