package dev.bnorm.elevated.service.auth

import org.springframework.security.core.GrantedAuthority

enum class Role(
    vararg startingAuthorities: Authority
) {
    ADMIN(
        Authority.DEVICES_ADMIN,
        Authority.READINGS_ADMIN,
        Authority.SENSORS_ADMIN,
        Authority.USERS_ADMIN,
    ),

    USER(
        Authority.DEVICES_READ,
        Authority.SENSORS_READ,
        Authority.READINGS_READ,
    ),

    DEVICE(
        Authority.DEVICES_READ,
        Authority.READINGS_WRITE,
    ),

    ;

    val authorities: Set<Authority> = setOf(*startingAuthorities).flatten().toSet()

    private fun Iterable<Authority>.flatten(): Sequence<Authority> {
        return sequence {
            for (authority in this@flatten) {
                yield(authority)
                yieldAll(authority.inherits.flatten())
            }
        }
    }
}

fun GrantedAuthority.toRole(): Role? =
    Role.values().find { role -> role.name == authority }
