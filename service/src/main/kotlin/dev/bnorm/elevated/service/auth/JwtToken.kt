package dev.bnorm.elevated.service.auth

import kotlinx.serialization.Serializable

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
