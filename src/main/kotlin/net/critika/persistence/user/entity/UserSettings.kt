package net.critika.persistence.user.entity

import kotlinx.uuid.exposed.KotlinxUUIDTable
import net.critika.domain.user.model.Language
import org.joda.time.DateTime

object UserSettings : KotlinxUUIDTable("user_settings") {
    val userId = reference("user_id", Users).uniqueIndex()
    val publicVisibility = bool("public_visibility").default(false)
    val pushNotifications = bool("push_notifications").default(false)
    val language = enumerationByName("language", 10, Language::class).default(Language.ENGLISH)
    val promotion = bool("promotion_status").default(false).nullable()
    val createdAt = long("created_at").default(DateTime.now().millis)
    val updatedAt = long("updated_at")
}
