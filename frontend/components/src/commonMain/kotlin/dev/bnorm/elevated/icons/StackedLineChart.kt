package dev.bnorm.elevated.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Filled.StackedLineChart: ImageVector
    get() {
        if (_stackedLineChart != null) {
            return _stackedLineChart!!
        }
        _stackedLineChart = materialIcon(name = "Filled.StackedLineChart") {
            materialPath {
                moveTo(2.0f, 19.99f)
                lineToRelative(7.5f, -7.51f)
                lineToRelative(4.0f, 4.0f)
                lineToRelative(7.09f, -7.97f)
                lineTo(22.0f, 9.92f)
                lineToRelative(-8.5f, 9.56f)
                lineToRelative(-4.0f, -4.0f)
                lineToRelative(-6.0f, 6.01f)
                lineTo(2.0f, 19.99f)
                close()
                moveTo(3.5f, 15.49f)
                lineToRelative(6.0f, -6.01f)
                lineToRelative(4.0f, 4.0f)
                lineTo(22.0f, 3.92f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-7.09f, 7.97f)
                lineToRelative(-4.0f, -4.0f)
                lineTo(2.0f, 13.99f)
                lineTo(3.5f, 15.49f)
                close()
            }
        }
        return _stackedLineChart!!
    }

private var _stackedLineChart: ImageVector? = null
