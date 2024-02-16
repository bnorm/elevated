package dev.bnorm.elevated.log

import kotlin.reflect.KClass

expect fun getLogger(name: String): Logger
fun getLogger(clazz: KClass<*>): Logger = getLogger(clazz.qualifiedName!!)
inline fun <reified T> getLogger(): Logger = getLogger(T::class)

interface Logger {
    fun trace(t: Throwable? = null, msg: () -> String)
    fun debug(t: Throwable? = null, msg: () -> String)
    fun info(t: Throwable? = null, msg: () -> String)
    fun warn(t: Throwable? = null, msg: () -> String)
    fun error(t: Throwable? = null, msg: () -> String)
}
