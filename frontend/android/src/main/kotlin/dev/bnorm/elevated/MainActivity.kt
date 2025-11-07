package dev.bnorm.elevated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.bnorm.elevated.di.graph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screen = application.graph.mainScreen
        setContent { screen.Render() }
    }
}
