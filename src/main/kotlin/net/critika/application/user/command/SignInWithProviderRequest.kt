package net.critika.application.user.command

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithProviderRequest(val email: String, val username: String, val deviceToken: String)
