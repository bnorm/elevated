package dev.bnorm.elevated.ui.screen

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.ViewModel
import dev.bnorm.elevated.state.pump.PumpModel
import dev.bnorm.elevated.state.pump.PumpPresenter
import dev.bnorm.elevated.state.pump.PumpViewEvent
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class PumpViewModel(
    private val client: ElevatedClient,
) : ViewModel<PumpViewEvent, NetworkResult<PumpModel>>() {
    suspend fun dispense(pumpId: PumpId, deviceId: DeviceId, amount: Double) {
        client.submitDeviceAction(
            deviceId = deviceId,
            request = DeviceActionPrototype(
                args = PumpDispenseArguments(
                    pumpId = pumpId,
                    amount = amount
                )
            )
        )
    }

    @Composable
    override fun models(events: Flow<PumpViewEvent>): NetworkResult<PumpModel> {
        return PumpPresenter(client, events)
    }
}
