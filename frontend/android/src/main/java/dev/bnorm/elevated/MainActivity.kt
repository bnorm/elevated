package dev.bnorm.elevated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.android.AndroidInjection
import dev.bnorm.elevated.ui.MainComponent
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var component: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContent { component.render() }
    }
}

