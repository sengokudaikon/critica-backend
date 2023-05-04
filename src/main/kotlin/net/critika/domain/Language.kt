package net.critika.domain

enum class Language {
    ENGLISH,
    SPANISH,
    FRENCH,
    GERMAN,
    DUTCH,
    RUSSIAN,
    ARMENIAN,
    UKRAINIAN,
    KAZAKH,
    BELARUSIAN;

    companion object {
        fun shortValueOf(it: String): String {
            return when (it) {
                ENGLISH.toString() -> "en"
                SPANISH.toString() -> "es"
                FRENCH.toString() -> "fr"
                GERMAN.toString() -> "de"
                DUTCH.toString() -> "nl"
                RUSSIAN.toString() -> "ru"
                ARMENIAN.toString() -> "hy"
                UKRAINIAN.toString() -> "ua"
                KAZAKH.toString() -> "kz"
                BELARUSIAN.toString() -> "by"
                else -> {
                    throw IllegalArgumentException("Language $it is not supported")}
            }
        }
    }
}