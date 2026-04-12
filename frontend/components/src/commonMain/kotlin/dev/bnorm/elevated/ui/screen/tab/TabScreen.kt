package dev.bnorm.elevated.ui.screen.tab

import androidx.compose.ui.graphics.vector.ImageVector
import dev.bnorm.elevated.ui.screen.Screen

interface TabScreen : Screen {
    val index: Int
    val label: String
    val icon: ImageVector
    val route: String get() = label
}
