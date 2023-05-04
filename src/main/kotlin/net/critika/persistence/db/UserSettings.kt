package net.critika.persistence.db

import net.critika.domain.Language
import org.jetbrains.exposed.dao.id.UUIDTable
import org.joda.time.DateTime

object UserSettings : UUIDTable("user_settings") {
    val userId = reference("user_id", Users).uniqueIndex()
    val emailVerified = bool("email_verified").default(false)
    val publicVisibility = bool("public_visibility").default(false)
    val pushNotifications = bool("push_notifications").default(false)
    val language = enumerationByName("language", 10, Language::class).default(Language.ENGLISH)
    val promotion = bool("promotion_status").default(false).nullable()
    val createdAt = long("created_at").default(DateTime.now().millis)
    val updatedAt = long("updated_at")
}