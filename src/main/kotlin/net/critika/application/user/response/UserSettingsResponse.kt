package net.critika.application.user.response

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsResponse(
    val id: String,
    val username: String,
    val emailConfirmed: Boolean,
    val publicVisibility: Boolean,
    val pushNotificationsEnabled: Boolean,
    val language: String,
    val promoted: Boolean?,
)
