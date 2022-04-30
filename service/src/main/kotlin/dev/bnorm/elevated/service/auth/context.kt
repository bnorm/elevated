package dev.bnorm.elevated.service.auth

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

suspend fun currentSecurityContext(): SecurityContext? =
    ReactiveSecurityContextHolder.getContext().awaitSingleOrNull()

suspend fun currentAuthentication(): Authentication? = currentSecurityContext()?.authentication

suspend fun currentJwtToken(): Jwt? {
    return (currentAuthentication() as? JwtAuthenticationToken)?.token
}

