package dev.bnorm.elevated.model.auth

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@RequiresOptIn
annotation class PasswordUsage

@Serializable
@JvmInline
value class Password(
    @property:PasswordUsage
    val value: String
) {
    override fun toString(): String = "*****"
}
