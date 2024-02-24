package dev.bnorm.elevated.service

import dev.bnorm.elevated.service.devices.DeviceWebSocketHandler
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.resource.VersionResourceResolver
import org.springframework.web.reactive.socket.WebSocketHandler

@Configuration
class WebConfig(
    private val deviceWebSocketHandler: DeviceWebSocketHandler,
) : WebFluxConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/ui/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS))
            .resourceChain(true)
            .addResolver(VersionResourceResolver().addContentVersionStrategy("/**"))
    }

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
