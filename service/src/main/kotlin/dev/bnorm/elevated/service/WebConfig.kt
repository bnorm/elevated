package dev.bnorm.elevated.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import java.net.URI


@Configuration
class WebConfig {

    @Bean
    fun routerFunction() = coRouter {
        GET("/") {
            ServerResponse.temporaryRedirect(URI.create("/ui/index.html")).build().awaitSingle()
        }
    }
}
