package dev.bnorm.elevated.service.notifications

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.notifications.NotificationCode
import dev.bnorm.elevated.model.notifications.NotificationCreateRequest
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.UserRegisterRequest
import dev.bnorm.elevated.service.notifications.db.NotificationEntity
import dev.bnorm.elevated.service.users.UserService
import dev.bnorm.elevated.test.container.DockerContainerConfig
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection

@SpringBootTest
@Import(DockerContainerConfig::class)
class NotificationServiceTest @Autowired constructor(
    private val userService: UserService,
    private val notificationService: NotificationService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<NotificationEntity>().awaitSingleOrNull()
    }

    @Test
    fun `can find notification`(): Unit = runBlocking {
        val user = userService.registerUser(
            request = UserRegisterRequest(
                email = Email("test@example.com"),
                password = Password("TestPassword"),
                name = "Test User",
            )
        )

        val expected = notificationService.submitNotification(
            userId = user.id,
            prototype = NotificationCreateRequest(
                code = NotificationCode.UNKNOWN,
                message = "Test Message",
            )
        )
        val actual = notificationService.getNotification(user.id, expected.id)
        assertEquals(expected, actual)
    }
}
