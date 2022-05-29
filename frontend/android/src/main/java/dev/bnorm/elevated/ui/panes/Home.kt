package dev.bnorm.elevated.ui.panes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdUnits
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.bnorm.elevated.client.ElevatedClient
import kotlinx.coroutines.Dispatchers

enum class HomeTab(
    val label: String,
    val icon: ImageVector,
) {
    Charts("Charts", Icons.Filled.StackedLineChart),
    Pumps("Pumps", Icons.Filled.RotateRight),
    Devices("Devices", Icons.Filled.AdUnits),
}

@Composable
fun Home(client: ElevatedClient) {
    val scope = rememberCoroutineScope { Dispatchers.Default }

    var selectedTab by remember { mutableStateOf(HomeTab.Charts) }

    val chartPaneState = remember(scope) { ChartPaneState(client, scope) }

    Scaffold(
        content = {
            Surface(
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(it)
            ) {
                when (selectedTab) {
                    HomeTab.Charts -> ChartPane(chartPaneState)
                    HomeTab.Pumps -> PumpsList(client)
                    HomeTab.Devices -> DevicePane(client)
                }
            }
        },
        bottomBar = {
            BottomNavigation {
                for (tab in HomeTab.values()) {
                    BottomNavigationItem(
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    )
}
