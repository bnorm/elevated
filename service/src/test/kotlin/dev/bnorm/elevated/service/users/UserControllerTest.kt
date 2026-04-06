package dev.bnorm.elevated.service.users

import dev.bnorm.elevated.model.auth.AuthenticatedUser
import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtTokenUsage
import dev.bnorm.elevated.model.auth.Password
import dev.bnorm.elevated.model.users.Email
import dev.bnorm.elevated.model.users.User
import dev.bnorm.elevated.model.users.UserLoginRequest
import dev.bnorm.elevated.model.users.UserPatchRequest
import dev.bnorm.elevated.model.users.UserRegisterRequest
import dev.bnorm.elevated.service.users.db.UserEntity
import dev.bnorm.elevated.test.container.DockerContainerConfig
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DockerContainerConfig::class)
class UserControllerTest @Autowired constructor(
    @param:LocalServerPort private val port: Int,
    private val userService: UserService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    suspend fun createUser(email: Email, password: Password, name: String): AuthenticatedUser {
        userService.registerUser(
            UserRegisterRequest(
                email = email,
                password = password,
                name = name,
            )
        )

        val user = userService.authenticateUser(
            UserLoginRequest(
                email = email,
                password = password,
            )
        )

        return requireNotNull(user) { "authentication failed" }
    }

    private lateinit var webTestClient: WebTestClient
    private lateinit var admin: AuthenticatedUser

    @BeforeEach
    fun setup(): Unit = runBlocking {
        admin = createUser(
            email = Email("user@example.com"),
            password = Password("password"),
            name = "User",
        )

        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<UserEntity>().awaitSingleOrNull()
    }

    @OptIn(JwtTokenUsage::class)
    private fun AuthorizationToken.toHeader(): String = "$type ${value.value}"

    @Test
    fun `can login`(): Unit = runBlocking {
        val result = webTestClient.post()
            .uri("/api/v1/users/login")
            .bodyValue(
                UserLoginRequest(
                    email = Email("user@example.com"),
                    password = Password("password"),
                )
            )
            .exchangeSuccessfully()
            .expectBody<AuthenticatedUser>()
            .returnResult()

        assertEquals(admin.user, result.responseBody?.user)
    }

    @Test
    fun `cannot login - wrong email`(): Unit = runBlocking {
        webTestClient.post()
            .uri("/api/v1/users/login")
            .bodyValue(
                UserLoginRequest(
                    email = Email("noone@example.com"),
                    password = Password("password"),
                )
            )
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @Test
    fun `cannot login - wrong password`(): Unit = runBlocking {
        webTestClient.post()
            .uri("/api/v1/users/login")
            .bodyValue(
                UserLoginRequest(
                    email = Email("user@example.com"),
                    password = Password("wrong"),
                )
            )
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @Test
    fun `cannot login - wrong everything`(): Unit = runBlocking {
        webTestClient.post()
            .uri("/api/v1/users/login")
            .bodyValue(
                UserLoginRequest(
                    email = Email("noone@example.com"),
                    password = Password("wrong"),
                )
            )
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @Test
    fun `can get current`(): Unit = runBlocking {
        val result = webTestClient.get()
            .uri("/api/v1/users/current")
            .header("Authorization", admin.token.toHeader())
            .exchangeSuccessfully()
            .expectBody<AuthenticatedUser>()
            .returnResult()

        assertEquals(admin.user, result.responseBody?.user)
    }

    @Test
    fun `cannot get current`(): Unit = runBlocking {
        webTestClient.get()
            .uri("/api/v1/users/current")
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @Test
    fun `can patch self`(): Unit = runBlocking {
        val result = webTestClient.patch()
            .uri("/api/v1/users/${admin.user.id}")
            .header("Authorization", admin.token.toHeader())
            .bodyValue(
                UserPatchRequest(
                    name = "Test"
                )
            )
            .exchangeSuccessfully()
            .expectBody<User>()
            .returnResult()

        assertEquals(admin.user.copy(name = "Test"), result.responseBody)
    }

    @Test
    fun `cannot patch self - no authentication`(): Unit = runBlocking {
        webTestClient.patch()
            .uri("/api/v1/users/${admin.user.id}")
            .bodyValue(
                UserPatchRequest(
                    name = "Test"
                )
            )
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @Test
    fun `cannot patch self - wrong user`(): Unit = runBlocking {
        webTestClient.patch()
            .uri("/api/v1/users/0")
            .header("Authorization", admin.token.toHeader())
            .bodyValue(
                UserPatchRequest(
                    name = "Test"
                )
            )
            .exchange()
            .expectStatus()
            .isForbidden()
    }
}
