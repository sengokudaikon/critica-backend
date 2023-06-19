package net.critika.application.user.response

import kotlinx.serialization.Serializable
import net.critika.application.Response

@Serializable
data class UserSettingsResponse(
    val id: String,
    val publicVisibility: Boolean,
    val pushNotificationsEnabled: Boolean,
    val language: String,
    val promoted: Boolean?,
) : Response
