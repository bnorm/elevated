package dev.bnorm.elevated.service

import com.nimbusds.jose.jwk.source.ImmutableSecret
import dev.bnorm.elevated.service.auth.AuthorityRoleHierarchy
import dev.bnorm.elevated.service.auth.ROLE_CLAIM
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.lang.Nullable
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = false)
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun jwtSecretKey(): SecretKey {
        val keyBytes = (System.getenv()["JWT_SECRET"] ?: "dummysecret")
            .toByteArray(StandardCharsets.UTF_8)
        val secret = if (keyBytes.size < 32) keyBytes.copyOf(32) else keyBytes
        return SecretKeySpec(secret, "HmacSHA256")
    }

    @Bean
    fun jwtDecoder(secretKey: SecretKey): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder.withSecretKey(secretKey).build()

    @Bean
    fun jwtEncoder(secretKey: SecretKey): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(secretKey))

    @Bean
    fun roleHierarchy(@Nullable handler: AbstractSecurityExpressionHandler<*>?): RoleHierarchy =
        AuthorityRoleHierarchy().apply {
            handler?.setRoleHierarchy(this)
        }

    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverter {
        val jwtGrantedAuthoritiesConverter = ReactiveJwtGrantedAuthoritiesConverterAdapter { jwt ->
            listOfNotNull(jwt.getClaimAsString(ROLE_CLAIM))
                .map { SimpleGrantedAuthority(it) }
        }

        return ReactiveJwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        }
    }

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        jwtDecoder: ReactiveJwtDecoder,
        jwtAuthenticationConverter: ReactiveJwtAuthenticationConverter
    ): SecurityWebFilterChain = http {
        csrf {
            disable()
        }
        authorizeExchange {
            // UI related paths
            authorize("/", permitAll)
            authorize("/ui/**", permitAll)

            // API related paths
            authorize("/api/v1/users/login", permitAll)
            authorize("/api/v1/devices/login", permitAll)
            authorize("/api/v1/devices/*/connect", permitAll)
            authorize("/api/v1/users/register", permitAll)
            authorize("/api/**", authenticated)

            // Catch-all
            authorize(anyExchange, authenticated)
        }
        oauth2ResourceServer {
            jwt {
                this.jwtDecoder = jwtDecoder
                this.jwtAuthenticationConverter = jwtAuthenticationConverter
            }
        }
    }
}
