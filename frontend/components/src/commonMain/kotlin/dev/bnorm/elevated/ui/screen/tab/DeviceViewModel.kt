package dev.bnorm.elevated.ui.screen.tab

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.state.ViewModel
import dev.bnorm.elevated.state.device.DeviceModel
import dev.bnorm.elevated.state.device.DevicePresenter
import dev.bnorm.elevated.state.device.DeviceViewEvent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@Inject
class DeviceViewModel(
    scope: CoroutineScope,
    private val client: ElevatedClient,
) : ViewModel<DeviceViewEvent, DeviceModel>(scope) {
    fun refresh() {
        take(DeviceViewEvent.Refresh)
    }

    @Composable
    override fun models(events: Flow<DeviceViewEvent>): DeviceModel {
        return DevicePresenter(client, events)
    }
}
