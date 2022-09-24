package dev.bnorm.elevated.state

import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.notifications.Notification
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class NotificationManager @Inject constructor(
    private val elevatedClient: ElevatedClient,
) {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    suspend fun updateNotifications(): List<Notification> {
        val user = elevatedClient.getCurrentUser().user
        val notifications = elevatedClient.getNotifications(user.id)
        val previousNotifications = _notifications.getAndUpdate { notifications }

        val previousNotificationIds = previousNotifications.map { it.id }.toSet()
        return notifications.filter { it.id !in previousNotificationIds }.sortedBy { it.submitted }
    }
}
