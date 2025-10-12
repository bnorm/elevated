package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.model.sensors.MeasurementType
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
    val measurement: MeasurementType,
) {
    Tmp(id = SensorId("68ebd42babc1449a36207c24"), bus = 1, device = 0x66, measurement = MeasurementType.TMP),
    Ph(id = SensorId("6278048e770bd023d5d971ea"), bus = 1, device = 0x63, measurement = MeasurementType.PH),
    Ec(id = SensorId("6278049d770bd023d5d971eb"), bus = 1, device = 0x64, measurement = MeasurementType.EC),
}

fun FakeSensorService(): SensorService {
    // Hardcode known sensors
    return SensorService(
        all = SensorType.entries.map {
            FakeSensor(
                it.id,
                it.measurement,
                when (it) {
                    SensorType.Ph -> {
                        { Random.nextDouble(6.1, 6.2) }
                    }
                    SensorType.Ec -> {
                        { Random.nextDouble(700.0, 701.0) }
                    }
                    SensorType.Tmp -> {
                        { Random.nextDouble(20.0, 25.0) }
                    }
                }
            )
        }
    )
}
