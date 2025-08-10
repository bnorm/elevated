package dev.bnorm.elevated.model.devices

import dev.bnorm.elevated.model.Optional
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.charts.ChartId
import kotlinx.serialization.Serializable

@Serializable
data class DevicePatchRequest(
    val name: Optional<String> = Optional.empty(),
    val key: Optional<Password> = Optional.empty(),
    val chartId: Optional<ChartId?> = Optional.empty(),
) {
    init {
        require(!name.isEmpty || !key.isEmpty || !chartId.isEmpty) {
            "At least one value is required in patch request"
        }
    }
}
