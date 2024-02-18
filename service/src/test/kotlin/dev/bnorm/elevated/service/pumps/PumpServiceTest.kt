package dev.bnorm.elevated.service.pumps

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceCreateRequest
import dev.bnorm.elevated.model.pumps.PumpContent
import dev.bnorm.elevated.model.pumps.PumpCreateRequest
import dev.bnorm.elevated.service.devices.DeviceService
import dev.bnorm.elevated.service.devices.db.DeviceEntity
import dev.bnorm.elevated.service.pumps.db.PumpEntity
import dev.bnorm.elevated.test.container.DockerContainerConfig
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAllAndRemove
import org.springframework.data.mongodb.core.query.Query

@SpringBootTest
@Import(DockerContainerConfig::class)
class PumpServiceTest @Autowired constructor(
    private val pumpService: PumpService,
    private val deviceService: DeviceService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.findAllAndRemove<PumpEntity>(Query()).asFlow().collect()
        mongoOperations.findAllAndRemove<DeviceEntity>(Query()).asFlow().collect()
    }

    @Test
    fun `can find pump`(): Unit = runBlocking {
        val device = deviceService.createDevice(
            prototype = DeviceCreateRequest(
                name = "Test Device",
                key = Password("Test Password")
            )
        )

        val expected = pumpService.createPump(
            request = PumpCreateRequest(
                name = "Test Pump",
                deviceId = device.id,
                flowRate = 1.0,
                content = PumpContent.GENERAL_HYDROPONICS_PH_UP,
            )
        )
        val actual = pumpService.getPump(expected.id)
        assertEquals(expected, actual)
    }
}
