package dev.bnorm.elevated.model.notifications

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPatchRequest(
    val message: String? = null,
)
