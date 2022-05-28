package dev.bnorm.elevated.service.auth

import dev.bnorm.elevated.model.auth.Role
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class AuthorizationService {
    fun isUser(userId: String, principal: Jwt): Boolean {
        return userId == principal.subject || principal.role == Role.ADMIN
    }
}
