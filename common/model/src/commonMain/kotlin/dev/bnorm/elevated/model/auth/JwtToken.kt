package dev.bnorm.elevated.model.auth

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@RequiresOptIn
annotation class JwtTokenUsage

@Serializable
@JvmInline
value class JwtToken(
    @property:JwtTokenUsage
    val value: String
) {
    override fun toString(): String = "*****"
}
