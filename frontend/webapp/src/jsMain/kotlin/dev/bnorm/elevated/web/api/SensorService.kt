package dev.bnorm.elevated.web.api

import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.Instant

object SensorService {
    suspend fun getSensorReadings(sensorId: SensorId, startTime: Instant): List<SensorReading> {
        return client.get(apiUrl.appendPath("sensors/${sensorId.value}/readings")) {
            parameter("startTime", startTime.toString())
        }
    }
}
