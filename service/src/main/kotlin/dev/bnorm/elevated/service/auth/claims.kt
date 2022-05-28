package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.model.auth.Role
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.User
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import java.time.Duration
import java.time.Instant

const val ROLE_CLAIM = "role"
const val EMAIL_CLAIM = "email"

val Jwt.role: Role?
    get() = getClaimAsString(ROLE_CLAIM)?.let { Role.byName[it] }

val Jwt.email: Email?
    get() = getClaimAsString(EMAIL_CLAIM)?.let { Email(it) }

fun User.toClaims(): JwtClaimsSet {
    val now = Instant.now()
    return JwtClaimsSet.builder()
        .subject(id.value)
        .issuedAt(now)
        .expiresAt(now + Duration.ofHours(24))
        .claim(EMAIL_CLAIM, email.value)
        .claim(ROLE_CLAIM, role)
        .build()
}

fun Device.toClaims(): JwtClaimsSet {
    val now = Instant.now()
    return JwtClaimsSet.builder()
        .subject(id.value)
        .issuedAt(now)
        .expiresAt(now + Duration.ofHours(24))
        .claim(ROLE_CLAIM, Role.DEVICE)
        .build()
}

fun JwtEncoder.encode(claims: JwtClaimsSet): Jwt {
    val headers = JwsHeader.with(MacAlgorithm.HS256)
        .type("JWT")
        .build()
    val parameters = JwtEncoderParameters.from(headers, claims)
    return encode(parameters)
}
