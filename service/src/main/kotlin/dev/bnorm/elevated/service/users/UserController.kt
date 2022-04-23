package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.users.User
import dev.bnorm.elevated.model.users.UserLoginRequest
import dev.bnorm.elevated.model.users.UserRegisterRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/register")
    suspend fun registerUser(@RequestBody request: UserRegisterRequest): User {
        return userService.registerUser(request)
    }

    @PostMapping("/login")
    suspend fun authenticateUser(@RequestBody request: UserLoginRequest): AuthenticatedUser {
        return userService.authenticateUser(request)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    @PreAuthorize("hasAuthority('USERS_ADMIN')")
    @GetMapping
    fun getAllUsers(): Flow<User> {
        return userService.getAllUsers()
    }
}
