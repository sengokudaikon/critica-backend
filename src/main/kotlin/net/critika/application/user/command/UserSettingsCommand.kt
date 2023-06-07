package net.critika.application.user.command

import kotlinx.uuid.UUID
import net.critika.domain.user.model.Language

interface UserSettingsCommand {
    data class Create(
        val userId: UUID,
        val language: String = Language.ENGLISH.name,
    ) : UserSettingsCommand

    sealed class Update(userId: UUID) : UserSettingsCommand {

        data class Username(
            val uid: UUID,
            val username: String,
        ) : Update(uid)

        data class Password(
            val uid: UUID,
            val password: String,
        ) : Update(uid)

        data class Email(
            val uid: UUID,
            val email: String,
        ) : Update(uid)

        data class PlayerName(
            val uid: UUID,
            val playerName: String,
        ) : Update(uid)

        data class DeviceToken(
            val uid: UUID,
            val deviceToken: String,
        ) : Update(uid)

        data class Language(
            val uid: UUID,
            val language: String,
        ) : Update(uid)

        data class EmailVerification(
            val uid: UUID,
            val emailVerification: Boolean,
        ) : Update(uid)

        data class PushNotification(
            val uid: UUID,
            val pushNotification: Boolean,
        ) : Update(uid)

        data class PublicVisibility(
            val uid: UUID,
            val publicVisibility: Boolean,
        ) : Update(uid)
    }
}
