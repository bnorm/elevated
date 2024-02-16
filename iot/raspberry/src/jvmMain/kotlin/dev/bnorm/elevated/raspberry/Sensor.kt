package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.sensors.SensorId

interface Sensor {
    val id: SensorId

    suspend fun read(): Double
}

class FakeSensor(
    override val id: SensorId,
    private val generator: () -> Double,
) : Sensor {
    companion object {
        private val log = getLogger<Sensor>()
    }

    override suspend fun read(): Double {
        val reading = generator()
        log.debug { "response for $id: value=$reading" }
        return reading
    }
}
