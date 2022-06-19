package dev.bnorm.elevated.service.notifications.db

import dev.bnorm.elevated.model.notifications.NotificationCode
import dev.bnorm.elevated.model.notifications.NotificationId
import dev.bnorm.elevated.model.users.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(NotificationEntity.COLLECTION_NAME)
class NotificationEntity(
    val userId: UserId,
    val code: NotificationCode,
    val message: String,
    val submitted: Instant,
    val acknowledged: Instant?,
) {
    @Id
    lateinit var _id: ObjectId
    val id: NotificationId get() = NotificationId(_id.toHexString())

    companion object {
        const val COLLECTION_NAME = "notifications"
    }
}
