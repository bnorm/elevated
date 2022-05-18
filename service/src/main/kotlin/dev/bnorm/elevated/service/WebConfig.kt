package dev.bnorm.elevated.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import java.net.URI

@Configuration
class WebConfig(
    private val deviceWebSocketHandler: DeviceWebSocketHandler,
) {
    @Bean
    fun routerFunction() = coRouter {
        GET("/") {
            ServerResponse.temporaryRedirect(URI.create("/ui/index.html")).build().awaitSingle()
        }
    }

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping? {
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = mapOf<String, WebSocketHandler>(
            DeviceWebSocketHandler.pattern.patternString to deviceWebSocketHandler,
        )
        return handlerMapping
    }
}
