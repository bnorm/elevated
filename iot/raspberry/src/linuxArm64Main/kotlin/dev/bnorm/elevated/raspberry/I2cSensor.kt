package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.sensors.SensorId
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun SensorService(): SensorService {
    val sensors = SensorType.entries.map {
        val name = "sensor" + it.id.toString().padStart(2, '0')
        AtlasI2cSensor(I2cDevice(address = it.device.toUInt(), bus = it.bus.toUInt()), name, it.id)
    }
    return SensorService(sensors)
}

private class AtlasI2cSensor(
    private val i2cDevice: I2cDevice,
    private val name: String,
    override val id: SensorId,
) : Sensor {
    companion object {
        private val log = getLogger<Sensor>()

        private const val READ_DELAY = 900L
    }

    private var mutex = Mutex()

    override suspend fun read(): Double {
        mutex.withLock {
            i2cDevice.write("r")
            delay(READ_DELAY)

            val buffer = i2cDevice.readBytes(31)
            val size = buffer.indexOf(0)
            val response = buffer.copyOf(size)
            log.debug { "response for $id: size=$size hex=${response.toHexString()}" }

            if (response[0].toInt() != 1) {
                throw Exception("code=" + response[0].toString(16))
            } else {
                return response.decodeToString(startIndex = 1, endIndex = size).toDouble()
            }
        }
    }

    override fun toString(): String = name
}
