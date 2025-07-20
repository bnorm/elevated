package dev.bnorm.elevated.service.notifications

import dev.bnorm.elevated.model.notifications.Notification
import dev.bnorm.elevated.model.notifications.NotificationCreateRequest
import dev.bnorm.elevated.model.notifications.NotificationId
import dev.bnorm.elevated.model.notifications.NotificationPatchRequest
import dev.bnorm.elevated.model.users.UserId
import dev.bnorm.elevated.service.notifications.db.NotificationEntity
import dev.bnorm.elevated.service.notifications.db.NotificationRepository
import dev.bnorm.elevated.service.notifications.db.NotificationUpdate
import dev.bnorm.elevated.service.users.UserService
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.toKotlinInstant
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val userService: UserService,
    private val notificationRepository: NotificationRepository,
) {
    // TODO Replace with a mongo change stream?
    private val notifications = MutableSharedFlow<Notification>(
        replay = 0,
        extraBufferCapacity = Channel.UNLIMITED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    suspend fun submitNotification(
        userId: UserId,
        prototype: NotificationCreateRequest,
    ): Notification {
        val notification = notificationRepository.insert(prototype.toEntity(userId)).toDto()
        notifications.emit(notification)
        return notification
    }

    suspend fun getNotification(userId: UserId, notificationId: NotificationId): Notification? {
        return notificationRepository.findById(userId, notificationId)?.toDto()
    }

    suspend fun updateNotification(
        userId: UserId,
        notificationId: NotificationId,
        request: NotificationPatchRequest,
    ): Notification? {
        return notificationRepository.modify(userId, notificationId, request.toUpdate())?.toDto()
    }

    suspend fun acknowledgeNotification(userId: UserId, notificationId: NotificationId): Notification? {
        val timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val notification = notificationRepository.acknowledge(userId, notificationId, timestamp)
        return notification?.toDto()
    }

    fun watchNotifications(userId: UserId): Flow<Notification> {
        return notifications.filter { it.userId == userId }
    }

    fun getNotifications(userId: UserId, acknowledged: Boolean = false, limit: Int? = null): Flow<Notification> {
        return notificationRepository.findByUserId(userId, acknowledged, limit).map { it.toDto() }
    }

    suspend fun deleteNotification(userId: UserId, notificationId: NotificationId) {
        notificationRepository.deleteById(userId, notificationId)
    }

    private fun NotificationEntity.toDto(): Notification {
        return Notification(
            id = id,
            userId = userId,
            code = code,
            message = message,
            submitted = submitted.toKotlinInstant(),
            acknowledged = acknowledged?.toKotlinInstant(),
        )
    }

    private fun NotificationCreateRequest.toEntity(userId: UserId): NotificationEntity {
        return NotificationEntity(
            userId = userId,
            code = code,
            message = message,
            submitted = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            acknowledged = null,
        )
    }

    private fun NotificationPatchRequest.toUpdate(): NotificationUpdate {
        return NotificationUpdate(
            message = message,
        )
    }
}
