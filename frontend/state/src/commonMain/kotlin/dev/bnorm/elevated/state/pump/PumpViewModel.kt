package dev.bnorm.elevated.state.pump

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.bnorm.elevated.model.pumps.Pump

data class PumpViewModel(
    val pump: Pump,
) {
    var on by mutableStateOf(false)
    var amount by mutableStateOf("")
}
