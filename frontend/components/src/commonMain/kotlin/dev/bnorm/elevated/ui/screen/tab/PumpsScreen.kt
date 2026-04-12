package dev.bnorm.elevated.ui.screen.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.bnorm.elevated.icons.RotateRight
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.state.NetworkResult
import dev.bnorm.elevated.state.pump.PumpModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Inject
@ContributesIntoSet(AppScope::class)
class PumpsScreen(
    private val viewModel: PumpViewModel,
) : TabScreen {
    override val index: Int get() = 1
    override val label: String get() = "Pumps"
    override val icon: ImageVector get() = Icons.Filled.RotateRight
    override val route: String get() = "/pumps"

    @Composable
    override fun Render() {
        val model by viewModel.models.collectAsState()

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            when (val result = model) {
                is NetworkResult.Loaded -> ShowPumps(result.value)
                is NetworkResult.Error -> Text(text = "Error loading pumps: ${result.error.message}")
                NetworkResult.Loading -> Text(text = "Loading pumps...")
            }
        }
    }

    @Composable
    private fun ShowPumps(model: PumpModel) {
        val scope = rememberCoroutineScope { Dispatchers.Default }
        // TODO move amounts to view model?
        val amounts = remember { model.pumps.map { it.id to "" }.toMutableStateMap() }

        fun dispenseAll() {
            scope.launch {
                val pumps = amounts.entries.filter { (_, it) -> (it.toDoubleOrNull() ?: 0.0) > 0.0 }
                for (entry in pumps) {
                    viewModel.dispense(
                        pumpId = entry.key,
                        deviceId = DeviceId("62780348770bd023d5d971e9"),
                        amount = entry.value.toDouble(),
                    )
                }
                for (entry in pumps) {
                    entry.setValue("")
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (model.pumps.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pump in model.pumps) {
                        val amount = amounts.getValue(pump.id)
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amounts[pump.id] = it },
                            isError = amount.isNotEmpty() && amount.toDoubleOrNull() == null,
                            label = { Text("${pump.name} pump (Milliliters)") },
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Number,
                            )
                        )
                    }
                }

                Button(onClick = { dispenseAll() }) {
                    Text(text = "Dispense")
                }
            }
        }
    }
}
