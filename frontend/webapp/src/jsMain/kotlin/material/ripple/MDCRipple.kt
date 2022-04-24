package material.ripple

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import material.core.jsObject
import material.core.Builder
import material.core.MDCDsl
import org.jetbrains.compose.web.dom.ElementScope

public data class MDCRippleOpts(var isUnbounded: Boolean = false)

/**
 * [JS API](https://github.com/material-components/material-components-web/tree/v13.0.0/packages/mdc-ripple)
 */
@MDCDsl
@Composable
public fun ElementScope<*>.MDCRipple(
  opts: Builder<MDCRippleOpts>? = null
) {
  val options = MDCRippleOpts().apply { opts?.invoke(this) }
  DisposableEffect(null) {
    val mdc = MDCRippleModule.MDCRipple.attachTo(
      element = scopeElement,
      opts = jsObject {
        isUnbounded = options.isUnbounded
      }
    )
    onDispose { mdc.destroy() }
  }
}
