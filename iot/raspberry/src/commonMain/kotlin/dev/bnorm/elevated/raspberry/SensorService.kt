package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.model.sensors.SensorId
import kotlin.random.Random

class SensorService(
    val all: List<Sensor>
) {
    private val map: Map<SensorId, Sensor> = all.associateBy { it.id }

    operator fun get(id: SensorId): Sensor? = map[id]
    operator fun get(type: SensorType): Sensor = map.getValue(type.id)
}

enum class SensorType(
    val id: SensorId,
    val bus: Int = 1,
    val device: Int,
) {
    Ph(id = SensorId("6278048e770bd023d5d971ea"), bus = 1, device = 0x63),
    Ec(id = SensorId("6278049d770bd023d5d971eb"), bus = 1, device = 0x64),
}

fun FakeSensorService(): SensorService {
    // Hardcode known sensors
    return SensorService(
        all = SensorType.values().map {
            FakeSensor(
                it.id, when (it) {
                    SensorType.Ph -> {
                        { Random.nextDouble(6.1, 6.2) }
                    }
                    SensorType.Ec -> {
                        { Random.nextDouble(700.0, 701.0) }
                    }
                }
            )
        }
    )
}
