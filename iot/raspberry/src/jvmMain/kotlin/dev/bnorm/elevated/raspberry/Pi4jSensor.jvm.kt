package dev.bnorm.elevated.raspberry

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import com.pi4j.io.i2c.I2C
import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.sensors.SensorId
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun Context.SensorService(): SensorService {
    // Hardcode known sensors
    return SensorService(
        all = SensorType.values().map { Sensor(it.id, it.bus, it.device) }
    )
}

fun Context.Sensor(
    id: SensorId,
    bus: Int = 1,
    device: Int,
): Sensor {
    val name = "sensor" + id.toString().padStart(2, '0')
    val config = I2C.newConfigBuilder(this)
        .id(name)
        .name(name)
        .bus(bus)
        .device(device)
        .provider("linuxfs-i2c")
    val i2c = create(config)
    return Pi4jSensor(i2c, name, id)
}

private class Pi4jSensor(
    private val i2c: I2C,
    private val name: String,
    override val id: SensorId,
) : Sensor {
    companion object {
        private val log = getLogger<Sensor>()
    }

    private var mutex = Mutex()
    private val buffer = ByteArray(31)

    override suspend fun read(): Double {
        mutex.withLock {
            i2c.write("r")
            delay(900)

            i2c.read(buffer)
            val size = buffer.indexOf(0)
            val copy = buffer.copyOf(size)
            log.debug {
                "response for $id: size=$size hex=${
                    copy.joinToString("") { it.toString(16).padStart(2, '0') }
                }"
            }

            if (copy[0].toInt() != 1) {
                throw IOException("code=" + copy[0].toString(16))
            } else {
                return copy.decodeToString(startIndex = 1, endIndex = size).toDouble()
            }
        }
    }

    override fun toString(): String = name
}
