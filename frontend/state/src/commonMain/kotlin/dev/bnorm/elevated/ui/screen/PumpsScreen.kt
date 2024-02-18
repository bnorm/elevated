package dev.bnorm.elevated.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.pump.PumpViewModel
import dev.bnorm.elevated.state.pump.PumpsPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PumpsScreen @Inject constructor(
    private val presenter: PumpsPresenter,
) {
    @Composable
    fun Render() {
        val scope = rememberCoroutineScope { Dispatchers.Default }

        presenter.present()
        val pumpsResult by presenter.pumps

        Column {
            when (val result = pumpsResult) {
                is NetworkResult.Loaded -> {
                    val pumps = result.value
                    if (pumps.isNotEmpty()) {
                        PumpAmountList(pumpStates = pumps)
                        Button(onClick = {
                            scope.launch {
                                val dispense = pumps.filter { (it.amount.toDoubleOrNull() ?: 0.0) > 0.0 }
                                for (state in dispense) {
                                    launch {
                                        presenter.dispense(
                                            pump = state.pump,
                                            deviceId = DeviceId("62780348770bd023d5d971e9"),
                                            amount = state.amount.toDouble(),
                                        )
                                        state.amount = ""
                                    }
                                }
                            }
                        }) {
                            Text(text = "Dispense")
                        }
                    }
                }

                is NetworkResult.Error -> Text(text = "Error loading pumps: ${result.error.message}")
                NetworkResult.Loading -> Text(text = "Loading pumps...")
            }
        }
    }

    @Composable
    private fun PumpAmountList(
        pumpStates: List<PumpViewModel>
    ) {
        Column {
            for (state in pumpStates) {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { state.amount = it },
                    isError = state.amount.isNotEmpty() && state.amount.toIntOrNull() == null,
                    label = { Text("${state.pump.name} pump (Milliliters)") },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                    )
                )
            }
        }
    }
}
