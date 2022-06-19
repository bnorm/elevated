package dev.bnorm.elevated.service.sensors

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.devices.DeviceCreateRequest
import dev.bnorm.elevated.model.sensors.SensorCreateRequest
import dev.bnorm.elevated.model.sensors.SensorType
import dev.bnorm.elevated.service.devices.DeviceService
import dev.bnorm.elevated.service.devices.db.DeviceEntity
import dev.bnorm.elevated.service.sensors.db.SensorEntity
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
class SensorServiceTest @Autowired constructor(
    private val sensorService: SensorService,
    private val deviceService: DeviceService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<SensorEntity>().awaitSingleOrNull()
        mongoOperations.dropCollection<DeviceEntity>().awaitSingleOrNull()
    }

    @Test
    fun `can find sensor`(): Unit = runBlocking {
        val device = deviceService.createDevice(
            prototype = DeviceCreateRequest(
                name = "Test Device",
                key = Password("Test Password")
            )
        )

        val expected = sensorService.createSensor(
            request = SensorCreateRequest(
                name = "Test Sensor",
                type = SensorType.EC,
                deviceId = device.id
            )
        )
        val actual = sensorService.getSensor(expected.id)
        assertEquals(expected, actual)
    }
}
