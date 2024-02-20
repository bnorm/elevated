package dev.bnorm.elevated.ui.screen

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.state.ViewModel
import dev.bnorm.elevated.state.device.DeviceModel
import dev.bnorm.elevated.state.device.DevicePresenter
import dev.bnorm.elevated.state.device.DeviceViewEvent
import kotlinx.coroutines.flow.Flow

class DeviceViewModel @Inject constructor(
    private val client: ElevatedClient,
) : ViewModel<DeviceViewEvent, DeviceModel>() {
    @Composable
    override fun models(events: Flow<DeviceViewEvent>): DeviceModel {
        return DevicePresenter(client, events)
    }
}
