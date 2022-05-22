package dev.bnorm.elevated.model.users

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class UserId(
    val value: String
) {
    override fun toString(): String = value
}
