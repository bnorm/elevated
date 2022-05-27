package dev.bnorm.elevated.service.auth

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize("#deviceId == principal.claims.sub")
annotation class IsDevice

@PreAuthorize("#userId == principal.claims.sub")
annotation class IsUser
