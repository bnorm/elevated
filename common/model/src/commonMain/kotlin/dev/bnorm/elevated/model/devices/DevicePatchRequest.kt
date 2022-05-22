package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.charts.ChartId
import kotlinx.serialization.Serializable

@Serializable
data class DevicePatchRequest(
    val name: String? = null,
    val key: Password? = null,
    val chartId: ChartId? = null,
) {
    init {
        require(name != null || key != null || chartId != null) {
            "At least one value is required in patch request"
        }
    }
}
