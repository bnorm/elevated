package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.users.*
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PreAuthorize("hasAuthority('USERS_ADMIN')")
    @PostMapping("/register")
    suspend fun registerUser(@RequestBody request: UserRegisterRequest): User {
        return userService.registerUser(request)
    }

    @GetMapping("/current")
    suspend fun getCurrentUser(@AuthenticationPrincipal jwt: Jwt): AuthenticatedUser {
        return userService.getCurrentUser(UserId(jwt.subject))
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/login")
    suspend fun authenticateUser(@RequestBody request: UserLoginRequest): AuthenticatedUser {
        return userService.authenticateUser(request)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
    }

    @PreAuthorize("@authorizationService.isUser(#userId, principal)")
    @PatchMapping("/{userId}")
    suspend fun patchUser(
        @PathVariable userId: UserId,
        @RequestBody request: UserPatchRequest,
    ): User {
        return userService.patchUserById(userId, request)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PreAuthorize("hasAuthority('USERS_ADMIN')")
    @GetMapping
    fun getAllUsers(): Flow<User> {
        return userService.getAllUsers()
    }
}
