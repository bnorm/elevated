package dev.bnorm.elevated.service.auth

import kotlinx.serialization.Serializable
import org.springframework.security.crypto.password.PasswordEncoder

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

@OptIn(PasswordUsage::class)
fun PasswordEncoder.encode(password: Password): String =
    encode(password.value)

@OptIn(PasswordUsage::class)
fun PasswordEncoder.matches(password: Password, encoded: String): Boolean =
    matches(password.value, encoded)
