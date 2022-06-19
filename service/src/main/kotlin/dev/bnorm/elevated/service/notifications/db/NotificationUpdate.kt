package dev.bnorm.elevated.service.notifications.db

class NotificationUpdate(
    val message: String? = null,
) {
    init {
        require(message != null) {
            "At least one value is required in update"
        }
    }
}
