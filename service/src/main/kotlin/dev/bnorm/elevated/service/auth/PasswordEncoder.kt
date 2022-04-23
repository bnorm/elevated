package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.auth.PasswordUsage
import org.springframework.security.crypto.password.PasswordEncoder

@OptIn(PasswordUsage::class)
fun PasswordEncoder.encode(password: Password): String =
    encode(password.value)

@OptIn(PasswordUsage::class)
fun PasswordEncoder.matches(password: Password, encoded: String): Boolean =
    matches(password.value, encoded)
