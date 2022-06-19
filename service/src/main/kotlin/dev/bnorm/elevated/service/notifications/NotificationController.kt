package dev.bnorm.elevated.service.notifications

import dev.bnorm.elevated.model.notifications.Notification
import dev.bnorm.elevated.model.notifications.NotificationId
import dev.bnorm.elevated.model.notifications.NotificationCreateRequest
import dev.bnorm.elevated.model.users.UserId
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/users/{userId}/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @PreAuthorize("hasAuthority('NOTIFICATIONS_WRITE')")
    @PostMapping
    suspend fun submitNotification(
        @PathVariable userId: UserId,
        @RequestBody prototype: NotificationCreateRequest,
    ): Notification {
        return notificationService.submitNotification(userId, prototype)
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS_READ')")
    @GetMapping
    fun getNotifications(
        @PathVariable userId: UserId,
        @RequestParam limit: Int?,
    ): Flow<Notification> {
        require(limit == null || limit > 0)
        return notificationService.getNotifications(userId, false, limit)
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS_READ')")
    @GetMapping("{notificationId}")
    suspend fun getNotification(
        @PathVariable userId: UserId,
        @PathVariable notificationId: NotificationId,
    ): Notification? {
        return notificationService.getNotification(userId, notificationId)
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS_WRITE')")
    @PutMapping("{notificationId}/acknowledge")
    suspend fun acknowledgeNotification(
        @PathVariable userId: UserId,
        @PathVariable notificationId: NotificationId,
    ): Notification? {
        return notificationService.acknowledgeNotification(userId, notificationId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("hasAuthority('NOTIFICATIONS_ADMIN')")
    @DeleteMapping("{notificationId}")
    suspend fun deleteNotification(
        @PathVariable userId: UserId,
        @PathVariable notificationId: NotificationId,
    ) {
        notificationService.deleteNotification(userId, notificationId)
    }
}
