package dev.bnorm.elevated.model.notifications

import kotlinx.serialization.Serializable

@Serializable
data class NotificationCreateRequest(
    val code: NotificationCode,
    val message: String,
)
