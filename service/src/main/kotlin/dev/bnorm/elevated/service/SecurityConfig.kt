package dev.bnorm.elevated.service

import com.nimbusds.jose.jwk.source.ImmutableSecret
import dev.bnorm.elevated.service.auth.AuthorityRoleHierarchy
import dev.bnorm.elevated.service.auth.ROLE_CLAIM
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
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
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
    fun jwtDecoder(): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder.withSecretKey(jwtSecretKey()).build()

    @Bean
    fun jwtEncoder(): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(jwtSecretKey()))

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
    fun springSecurityFilterChain(http: ServerHttpSecurity) = http {
        csrf {
            disable()
        }
        authorizeExchange {
            authorize("/ui/**", permitAll)
            authorize("/api/v1/users/login", permitAll)
            authorize("/api/v1/devices/login", permitAll)
            authorize("/api/v1/users/register", permitAll)
            authorize(anyExchange, authenticated)
        }
        oauth2ResourceServer {
            jwt {
                jwtDecoder = jwtDecoder()
                jwtAuthenticationConverter = jwtAuthenticationConverter()
            }
        }
    }
}
