package dev.bnorm.elevated.state.device

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock

class DevicesPresenter @Inject constructor(
    private val client: ElevatedClient,
) {
    private var devices by mutableStateOf<List<DeviceModel>?>(null)

    @Composable
    fun present(): List<DeviceModel>? {
        LaunchedEffect(Unit) {
            devices = client.getDeviceSummary()
        }

        return devices
    }

    private suspend fun ElevatedClient.getDeviceSummary(): List<DeviceModel> = coroutineScope {
        val devices = getDevices()
        val readings = devices
            .flatMap { it.sensors }.map { it.id }.distinct()
            .map { async { getLatestSensorReadings(it, count = 1).singleOrNull() } }.awaitAll()
            .filterNotNull().associateBy { it.sensorId }
        val actions = devices
            .map { async { it.id to getDeviceActions(it.id, submittedAfter = Clock.System.now() - 48.hours) } }
            .awaitAll()
            .toMap()

        devices.map { device ->
            DeviceModel(
                device = device,
                readings = device.sensors.mapNotNull { readings[it.id] }.associateBy { it.sensorId },
                actions = actions[device.id] ?: emptyList()
            )
        }
    }
}
