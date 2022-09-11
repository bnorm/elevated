package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.devices.DeviceActionPrototype
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.PumpDispenseArguments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PumpViewModel(
    val pump: Int,
    val name: String,
) {
    var on by mutableStateOf(false)
    var amount by mutableStateOf("")
}

@Composable
fun PumpsList(client: ElevatedClient) {
    val pumps = remember {
        mutableStateListOf(
            PumpViewModel(1, "pH"),
            PumpViewModel(2, "Micro"),
            PumpViewModel(3, "Gro"),
            PumpViewModel(4, "Bloom"),
        )
    }
    val scope = rememberCoroutineScope { Dispatchers.Default }

    Column {
        if (pumps.isNotEmpty()) {
            PumpAmountList(pumpStates = pumps)
            Button(onClick = {
                scope.launch {
                    val dispense = pumps.filter { (it.amount.toDoubleOrNull() ?: 0.0) > 0.0 }
                    for (state in dispense) {
                        launch {
                            client.submitDeviceAction(
                                deviceId = DeviceId("62780348770bd023d5d971e9"),
                                request = DeviceActionPrototype(
                                    args = PumpDispenseArguments(
                                        pump = state.pump,
                                        amount = state.amount.toDouble()
                                    )
                                )
                            )
                            state.amount = ""
                        }
                    }
                }
            }) {
                Text(text = "Dispense")
            }
        } else {
            Text(text = "Loading pumps...")
        }
    }
}

@Composable
fun PumpAmountList(
    pumpStates: List<PumpViewModel>
) {
    Column {
        for (state in pumpStates) {
            OutlinedTextField(
                value = state.amount,
                onValueChange = { state.amount = it },
                isError = state.amount.isNotEmpty() && state.amount.toIntOrNull() == null,
                label = { Text("${state.name} pump (Milliliters)") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                )
            )
        }
    }
}
