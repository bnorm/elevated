package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.model.auth.Role
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.users.User
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Duration
import java.time.Instant

const val ROLE_CLAIM = "role"

fun User.toClaims(): JwtClaimsSet {
    val now = Instant.now()
    return JwtClaimsSet.builder()
        .subject(id.value)
        .issuedAt(now)
        .expiresAt(now + Duration.ofHours(24))
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
