package dev.bnorm.elevated.web.components

import androidx.compose.runtime.Composable
import dev.bnorm.elevated.state.device.DevicesPresenter
import dev.bnorm.elevated.ui.screen.DevicesScreen
import dev.bnorm.elevated.web.api.client

private val devicesScreen = DevicesScreen(DevicesPresenter(client))

@Composable
fun WebDevices() {
    devicesScreen.Render()
}
