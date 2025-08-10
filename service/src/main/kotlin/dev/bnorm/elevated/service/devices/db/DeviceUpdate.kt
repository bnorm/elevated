package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.Optional
import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.devices.DeviceStatus
import java.time.Instant

class DeviceUpdate(
    val name: Optional<String> = Optional.empty(),
    val keyHash: Optional<String> = Optional.empty(),
    val status: Optional<DeviceStatus> = Optional.empty(),
    val lastActionTime: Optional<Instant?> = Optional.empty(),
    val chartId: Optional<ChartId?> = Optional.empty(),
) {
    init {
        require(!name.isEmpty || !keyHash.isEmpty || !status.isEmpty || !lastActionTime.isEmpty || !chartId.isEmpty) {
            "At least one value is required in update"
        }
    }
}
