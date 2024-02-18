package dev.bnorm.elevated.service.mongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfig {
    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                ChartIdReadingConverter(),
                ChartIdWritingConverter(),
                DeviceActionIdReadingConverter(),
                DeviceActionIdWritingConverter(),
                DeviceIdReadingConverter(),
                DeviceIdWritingConverter(),
                NotificationIdReadingConverter(),
                NotificationIdWritingConverter(),
                PumpIdReadingConverter(),
                PumpIdWritingConverter(),
                SensorIdReadingConverter(),
                SensorIdWritingConverter(),
                UserIdReadingConverter(),
                UserIdWritingConverter(),
            )
        )
    }
}
