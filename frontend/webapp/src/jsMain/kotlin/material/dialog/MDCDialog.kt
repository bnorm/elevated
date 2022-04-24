package material.dialog

import androidx.compose.runtime.Composable
import material.core.Builder
import material.core.ComposableBuilder
import material.core.MDCAttrsDsl
import material.core.MDCDsl
import material.core.MDCSideEffect
import material.core.aria
import material.core.initialiseMDC
import material.core.rememberUniqueDomElementId
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.ElementScope
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@JsModule("@material/dialog/dist/mdc.dialog.css")
@JsNonModule
private external val MDCDialogCSS: dynamic

public class MDCDialogAttrsScope(scope: AttrsScope<HTMLDivElement>) : AttrsScope<HTMLDivElement> by scope

public data class MDCDialogOpts(
  var open: Boolean = false,
  var fullscreen: Boolean = false,
  var scrimClickAction: String? = null,
  var escapeKeyAction: String? = null,
  var autoStackButtons: Boolean = true
)

public class MDCDialogScope(
  scope: ElementScope<HTMLDivElement>,
  internal val titleId: String,
  internal val contentId: String
) : ElementScope<HTMLDivElement> by scope

/**
 * [JS API](https://github.com/material-components/material-components-web/tree/v13.0.0/packages/mdc-dialog)
 */
@MDCDsl
@Composable
public fun MDCDialog(
  opts: Builder<MDCDialogOpts>? = null,
  attrs: Builder<MDCDialogAttrsScope>? = null,
  content: ComposableBuilder<MDCDialogScope>? = null
) {
  MDCDialogCSS
  val options = MDCDialogOpts().apply { opts?.invoke(this) }
  val titleId = rememberUniqueDomElementId()
  val contentId = rememberUniqueDomElementId()
  Div(
    attrs = {
      classes("mdc-dialog")
      if (options.fullscreen) classes(MDCDialogModule.cssClasses.FULLSCREEN)
      if (!options.autoStackButtons) classes(MDCDialogModule.cssClasses.STACKED)
      initialiseMDC(MDCDialogModule.MDCDialog::attachTo)
      attrs?.invoke(MDCDialogAttrsScope(this))
    }
  ) {
    MDCSideEffect<MDCDialogModule.MDCDialog>(options.open) {
      if (options.open) open() else close("")
    }
    MDCSideEffect(options.scrimClickAction, MDCDialogModule.MDCDialog::scrimClickAction)
    MDCSideEffect(options.escapeKeyAction, MDCDialogModule.MDCDialog::escapeKeyAction)
    MDCSideEffect(options.autoStackButtons, MDCDialogModule.MDCDialog::autoStackButtons)

    Div(attrs = { classes("mdc-dialog__container") }) {
      Div(
        attrs = {
          classes("mdc-dialog__surface")
          attr("role", if (options.fullscreen) "dialog" else "alertdialog")
          aria("modal", "true")
          aria("labelledby", titleId)
          aria("describedby", contentId)
        },
        content = content?.let { { MDCDialogScope(this, titleId, contentId).it() } }
      )
    }
    Div(attrs = { classes("mdc-dialog__scrim") })
  }
}

/**
 * [JS API](https://github.com/material-components/material-components-web/tree/v13.0.0/packages/mdc-dialog)
 */
@MDCAttrsDsl
public fun AttrsScope<out HTMLElement>.mdcDialogInitialFocus() {
  attr(MDCDialogModule.strings.INITIAL_FOCUS_ATTRIBUTE, "true")
}
