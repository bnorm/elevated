package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.service.auth.AuthenticatedUser
import dev.bnorm.elevated.service.auth.AuthorizationToken
import dev.bnorm.elevated.service.auth.JwtToken
import dev.bnorm.elevated.service.auth.Role
import dev.bnorm.elevated.service.auth.encode
import dev.bnorm.elevated.service.auth.matches
import dev.bnorm.elevated.service.auth.toClaims
import dev.bnorm.elevated.service.users.db.UserEntity
import dev.bnorm.elevated.service.users.db.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
) {
    fun getAllUsers(): Flow<User> {
        return userRepository.getAllUsers().map { it.toDto() }
    }

    suspend fun authenticateUser(request: UserLoginRequest): AuthenticatedUser? {
        val findByEmail = userRepository.findByEmail(request.email) ?: run {
            delay(500) // Fake work
            return null
        }
        return findByEmail.takeIf {
            passwordEncoder.matches(request.password, it.passwordHash)
        }?.toDto()?.toAuthenticatedUser()
    }

    suspend fun registerUser(request: UserRegisterRequest): User {
        return userRepository.insert(request.toEntity()).toDto()
    }

    private fun UserEntity.toDto(): User {
        return User(
            id = UserId(id),
            email = Email(email),
            name = name,
            role = role,
        )
    }

    private fun UserRegisterRequest.toEntity() = UserEntity(
        email = email.value.lowercase(),
        passwordHash = passwordEncoder.encode(password),
        name = name,
        role = Role.USER,
    )

    private fun User.toAuthenticatedUser(): AuthenticatedUser {
        val claims = toClaims()
        val jwt = jwtEncoder.encode(claims)
        return AuthenticatedUser(
            token = AuthorizationToken(
                type = "Bearer",
                value = JwtToken(jwt.tokenValue),
            ),
            user = this,
            authorities = role.authorities
        )
    }
}
