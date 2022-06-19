package dev.bnorm.elevated.model.notifications

import dev.bnorm.elevated.model.users.UserId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: NotificationId,
    val userId: UserId,
    val code: NotificationCode,
    val message: String,
    val submitted: Instant,
    val acknowledged: Instant?,
)
