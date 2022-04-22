package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.sensors.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
class DeviceAction(
    val id: DeviceActionId,
    val deviceId: DeviceId,
    @Serializable(with = InstantSerializer::class) val submitted: Instant,
    @Serializable(with = InstantSerializer::class) val completed: Instant?,
    val args: DeviceActionArguments,
)
