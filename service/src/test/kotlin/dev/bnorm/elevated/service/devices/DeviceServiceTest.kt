package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceCreateRequest
import dev.bnorm.elevated.service.devices.db.DeviceEntity
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
class DeviceServiceTest @Autowired constructor(
    private val deviceService: DeviceService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<DeviceEntity>().awaitSingleOrNull()
    }

    @Test
    fun `can find device`(): Unit = runBlocking {
        val expected = deviceService.createDevice(
            prototype = DeviceCreateRequest(
                name = "Test Device",
                key = Password("Test Password")
            )
        )
        val actual = deviceService.getDeviceById(expected.id)
        assertEquals(expected, actual)
    }
}
