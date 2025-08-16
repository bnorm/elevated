package dev.bnorm.elevated.ui.screen

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.state.ViewModel
import dev.bnorm.elevated.state.device.DeviceModel
import dev.bnorm.elevated.state.device.DevicePresenter
import dev.bnorm.elevated.state.device.DeviceViewEvent
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class DeviceViewModel(
    private val client: ElevatedClient,
) : ViewModel<DeviceViewEvent, DeviceModel>() {
    fun refresh() {
        take(DeviceViewEvent.Refresh)
    }

    @Composable
    override fun models(events: Flow<DeviceViewEvent>): DeviceModel {
        return DevicePresenter(client, events)
    }
}
