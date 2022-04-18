package dev.bnorm.elevated.service.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Authority(
    vararg inherits: Authority
) {
    DEVICES_READ,
    DEVICES_WRITE(DEVICES_READ),
    DEVICES_ADMIN(DEVICES_WRITE),

    READINGS_READ,
    READINGS_WRITE(READINGS_READ),
    READINGS_ADMIN(READINGS_WRITE),

    SENSORS_READ,
    SENSORS_WRITE(SENSORS_READ),
    SENSORS_ADMIN(SENSORS_WRITE),

    USERS_READ,
    USERS_WRITE(USERS_READ),
    USERS_ADMIN(USERS_WRITE),

    ;

    val inherits: Set<Authority> = setOf(*inherits)
}

fun Authority.toGrantedAuthority(): GrantedAuthority = SimpleGrantedAuthority(name)
