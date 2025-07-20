package dev.bnorm.elevated.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DefaultMonotonicFrameClock
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ViewModel<Event, Model> {
    // TODO link this to a better Job so it is cancelled properly.
    //  android = application lifecycle?
    //  web = custom?
    @Suppress("DEPRECATION")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main + DefaultMonotonicFrameClock)

    // Events have a capacity large enough to handle simultaneous UI events, but
    // small enough to surface issues if they get backed up for some reason.
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)

    val models: StateFlow<Model> by lazy(LazyThreadSafetyMode.NONE) {
        scope.launchMolecule(mode = RecompositionMode.ContextClock) {
            models(events)
        }
    }

    protected fun take(event: Event) {
        if (!events.tryEmit(event)) {
            error("Event buffer overflow.")
        }
    }

    @Composable
    protected abstract fun models(events: Flow<Event>): Model
}
