package dev.bnorm.elevated.state.device

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.asyncMap
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceAction
import dev.bnorm.elevated.model.sensors.MeasurementType
import dev.bnorm.elevated.model.sensors.SensorId
import dev.bnorm.elevated.model.sensors.SensorReading
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.valueOrNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class DeviceViewEvent {
    data object Refresh : DeviceViewEvent()
}

data class DeviceModel(
    val devices: List<Summary>
) {
    data class Summary(
        val device: Device,
        val readings: Map<SensorId, SensorReading>,
        val actions: List<DeviceAction>,
    )
}

@Composable
fun DevicePresenter(
    client: ElevatedClient,
    events: Flow<DeviceViewEvent>,
): DeviceModel {
    var instant by remember { mutableStateOf(Clock.System.now()) }
    var model by remember { NetworkResult.stateOf<DeviceModel>() }

    // Load devices.
    LaunchedEffect(instant) {
        withContext(Dispatchers.Default) {
            val now = instant
            model = NetworkResult.of { getDeviceModel(client, now) }
        }
    }

    // Processes incoming events.
    LaunchedEffect(events) {
        events.collect {
            when (it) {
                DeviceViewEvent.Refresh -> {
                    instant = Clock.System.now()
                }
            }
        }
    }

    return model.valueOrNull ?: DeviceModel(emptyList())
}

private suspend fun getDeviceModel(
    client: ElevatedClient,
    now: Instant
): DeviceModel {
    val devices = client.getDevices()

    val summaries = devices.asyncMap { device ->
        coroutineScope {
            val actions = async {
                client.getDeviceActions(device.id, submittedAfter = now - 7.days)
            }

            val latestReadings = device.sensors
                .asyncMap { client.getLatestSensorReadings(it.id, count = 1) }
                .mapNotNull { it.singleOrNull() }
                .map { reading ->
                    when (device.sensors.find { reading.sensorId == it.id }?.type) {
                        MeasurementType.TMP -> reading.copy(value = reading.value * 9.0 / 5.0 + 32.0)
                        else -> reading
                    }
                }
                .associateBy { it.sensorId }

            DeviceModel.Summary(
                device = device,
                readings = latestReadings,
                actions = actions.await()
            )
        }
    }

    return DeviceModel(devices = summaries)
}
