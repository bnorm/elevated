package dev.bnorm.elevated.test.container

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer

@TestConfiguration
class DockerContainerConfig {
    @Bean
    fun mongoDbContainer(): MongoDBContainer {
        val mongoDbContainer = MongoDBContainer("mongo:5.0")
        mongoDbContainer.withLogConsumer(containerLogsConsumer(LoggerFactory.getLogger("dev.bnorm.elevated.test.container.mongodb")))
        mongoDbContainer.start()
        return mongoDbContainer
    }

    @Bean
    @Primary
    fun mongoDbContainerProperties(mongoDbContainer: MongoDBContainer): MongoProperties {
        val mongoProperties = MongoProperties()
        mongoProperties.uri = mongoDbContainer.replicaSetUrl
        return mongoProperties
    }

    companion object {
        private fun containerLogsConsumer(log: Logger): Consumer<OutputFrame> = Consumer {
            if (it.type == OutputFrame.OutputType.STDERR) {
                log.warn(it.utf8String.trim())
            } else {
                log.debug(it.utf8String.trim())
            }
        }
    }
}
