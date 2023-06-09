package net.critika.application.user.command

import kotlinx.uuid.UUID
import net.critika.domain.user.model.Language

interface UserSettingsCommand {
    data class Create(
        val userId: UUID,
        val language: String = Language.ENGLISH.name,
    ) : UserSettingsCommand

    sealed class Update(uid: UUID) : UserSettingsCommand {

        data class PlayerName(
            val uid: UUID,
            val playerName: String,
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
