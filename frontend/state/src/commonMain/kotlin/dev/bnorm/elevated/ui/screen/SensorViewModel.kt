package dev.bnorm.elevated.ui.screen

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.client.ElevatedClient
import dev.bnorm.elevated.inject.Inject
import dev.bnorm.elevated.state.ViewModel
import dev.bnorm.elevated.state.sensor.SensorModel
import dev.bnorm.elevated.state.sensor.SensorPresenter
import dev.bnorm.elevated.state.sensor.SensorViewEvent
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

class SensorViewModel @Inject constructor(
    private val client: ElevatedClient,
) : ViewModel<SensorViewEvent, SensorModel>() {
    fun refresh() {
        take(SensorViewEvent.Refresh)
    }

    fun setDuration(duration: Duration) {
        take(SensorViewEvent.SetDuration(duration))
    }

    @Composable
    override fun models(events: Flow<SensorViewEvent>): SensorModel {
        return SensorPresenter(client, events)
    }
}
