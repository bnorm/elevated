package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.sensors.MeasurementType
import dev.bnorm.elevated.model.sensors.SensorId
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun SensorService(): SensorService {
    val tmpId = SensorId("68ebd42babc1449a36207c24")
    val tmp = AtlasI2cSensor(
        i2cDevice = I2cDevice(address = 0x66u, bus = 1u),
        name = "sensor" + tmpId.toString().padStart(2, '0'),
        id = tmpId,
        type = MeasurementType.TMP,
    )

    val phId = SensorId("6278048e770bd023d5d971ea")
    val ph = TemperatureCompensatedAtlasI2cSensor(
        i2cDevice = I2cDevice(address = 0x63u, bus = 1u),
        name = "sensor" + phId.toString().padStart(2, '0'),
        id = phId,
        type = MeasurementType.PH,
        temperatureSensor = tmp,
    )

    val ecId = SensorId("6278049d770bd023d5d971eb")
    val ec = TemperatureCompensatedAtlasI2cSensor(
        i2cDevice = I2cDevice(address = 0x64u, bus = 1u),
        name = "sensor" + ecId.toString().padStart(2, '0'),
        id = ecId,
        type = MeasurementType.EC,
        temperatureSensor = tmp,
    )

    return SensorService(listOf(tmp, ph, ec))
}

private class AtlasI2cSensor(
    private val i2cDevice: I2cDevice,
    private val name: String,
    override val id: SensorId,
    override val type: MeasurementType,
) : Sensor {
    companion object {
        private val log = getLogger<Sensor>()

        private const val READ_DELAY = 900L
        private val READ_BUFFER = 10.seconds
    }

    private var mutex = Mutex()

    private var last: Double = 0.0
    private var lastTimestamp: Instant = Instant.DISTANT_PAST

    override suspend fun read(): Double {
        val now = Clock.System.now()
        if ((now - lastTimestamp) < READ_BUFFER) return last

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
                val reading = response.decodeToString(startIndex = 1, endIndex = size).toDouble()
                last = reading
                lastTimestamp = now
                return reading
            }
        }
    }

    override fun toString(): String = name
}

private class TemperatureCompensatedAtlasI2cSensor(
    private val i2cDevice: I2cDevice,
    private val name: String,
    override val id: SensorId,
    override val type: MeasurementType,
    private val temperatureSensor: Sensor,
) : Sensor {
    companion object {
        private val log = getLogger<Sensor>()

        private const val READ_DELAY = 900L
        private val READ_BUFFER = 10.seconds
    }

    private var mutex = Mutex()

    private var last: Double = 0.0
    private var lastTimestamp: Instant = Instant.DISTANT_PAST

    override suspend fun read(): Double {
        val now = Clock.System.now()
        if ((now - lastTimestamp) < READ_BUFFER) return last

        mutex.withLock {
            val tmp = temperatureSensor.read()

            i2cDevice.write("rt,$tmp")
            delay(READ_DELAY)

            val buffer = i2cDevice.readBytes(31)
            val size = buffer.indexOf(0)
            val response = buffer.copyOf(size)
            log.debug { "response for $id: size=$size hex=${response.toHexString()}" }

            if (response[0].toInt() != 1) {
                throw Exception("code=" + response[0].toString(16))
            } else {
                val reading = response.decodeToString(startIndex = 1, endIndex = size).toDouble()
                last = reading
                lastTimestamp = now
                return reading
            }
        }
    }

    override fun toString(): String = name
}
