package material.ripple

import material.core.MDCBaseModule
import org.w3c.dom.Element

@JsModule("@material/ripple")
@JsNonModule
public external object MDCRippleModule {
  public interface MDCRippleAttachOpts {
    public var isUnbounded: Boolean?
  }

  public class MDCRipple(element: Element, opts: MDCRippleAttachOpts = definedExternally) : MDCBaseModule.MDCComponent<dynamic> {
    public companion object {
      public fun attachTo(element: Element, opts: MDCRippleAttachOpts = definedExternally): MDCRipple
    }
  }
}
