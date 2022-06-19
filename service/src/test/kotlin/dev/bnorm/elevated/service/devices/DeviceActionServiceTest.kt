package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceCreateRequest
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
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
class DeviceActionServiceTest @Autowired constructor(
    private val deviceService: DeviceService,
    private val deviceActionService: DeviceActionService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<DeviceEntity>().awaitSingleOrNull()
    }

    @Test
    fun `can find device action`(): Unit = runBlocking {
        val device = deviceService.createDevice(
            prototype = DeviceCreateRequest(
                name = "Test Device",
                key = Password("Test Password"),
            )
        )

        val expected = deviceActionService.submitDeviceAction(
            deviceId = device.id,
            prototype = DeviceActionPrototype(
                args = PumpDispenseArguments(
                    pump = 1,
                    amount = 1.0,
                )
            )
        )
        val actual = deviceActionService.getAction(device.id, expected.id)
        assertEquals(expected, actual)
    }
}
