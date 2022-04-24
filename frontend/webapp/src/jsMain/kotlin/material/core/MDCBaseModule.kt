package material.core

@JsModule("@material/base")
@JsNonModule
public external object MDCBaseModule {
  public abstract class MDCComponent<F> {
    public fun destroy()
    public fun initialize(vararg _args: Any?)
    public fun initialSyncWithDOM()
    public fun getDefaultFoundation(): F
    public val foundation: F
  }
}
