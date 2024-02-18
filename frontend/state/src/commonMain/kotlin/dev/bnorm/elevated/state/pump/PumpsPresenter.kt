package dev.bnorm.elevated.state.pump

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.state.NetworkResult

class PumpsPresenter @Inject constructor(
    private val client: ElevatedClient,
) {
    var pumps = mutableStateOf<NetworkResult<List<PumpViewModel>>>(NetworkResult.Loading)

    @Composable
    fun present() {
        LaunchedEffect(Unit) {
            val models = client.getPumps().map { pump ->
                PumpViewModel(
                    pump = pump,
                )
            }
            pumps.value = NetworkResult.Loaded(models)
        }
    }

    suspend fun dispense(pump: Pump, deviceId: DeviceId, amount: Double) {
        client.submitDeviceAction(
            deviceId = deviceId,
            request = DeviceActionPrototype(
                args = PumpDispenseArguments(
                    pumpId = pump.id,
                    amount = amount
                )
            )
        )
    }
}
