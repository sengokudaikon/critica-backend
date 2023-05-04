package net.critika.infrastructure.validation

class PlayerNameValidator : Validator<String> {
    override fun validate(value: String): Boolean {
        val usernamePattern = "^[A-Za-zА-Яа-я0-9]+(\\s[A-Za-zА-Яа-я0-9]+)*\$"
        val usernameRegex = Regex(usernamePattern)
        return usernameRegex.matches(value)
    }
}