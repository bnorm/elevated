package dev.bnorm.elevated.service.devices.db

import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.devices.DeviceStatus
import java.time.Instant

class DeviceUpdate(
    val name: String? = null,
    val keyHash: String? = null,
    val status: DeviceStatus? = null,
    val lastActionTime: Instant? = null,
    val chartId: ChartId? = null,
) {
    init {
        require(name != null || keyHash != null || status != null || lastActionTime != null || chartId != null) {
            "At least one value is required in update"
        }
    }
}
