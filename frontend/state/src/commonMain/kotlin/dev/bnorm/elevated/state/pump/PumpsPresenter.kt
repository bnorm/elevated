package dev.bnorm.elevated.state.pump

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.model.pumps.Pump
import dev.bnorm.elevated.state.NetworkResult
import kotlinx.coroutines.flow.Flow

sealed class PumpViewEvent

data class PumpModel(
    val pumps: List<Pump>,
)

@Composable
fun PumpPresenter(
    client: ElevatedClient,
    events: Flow<PumpViewEvent>,
): NetworkResult<PumpModel> {
    var model by remember { NetworkResult.stateOf<PumpModel>() }

    LaunchedEffect(client) {
        model = NetworkResult.of { getPumpModel(client) }
    }

    // Processes incoming events.
    LaunchedEffect(events) {
        events.collect {
        }
    }

    return model
}

private suspend fun getPumpModel(client: ElevatedClient): PumpModel {
    return PumpModel(client.getPumps())
}
