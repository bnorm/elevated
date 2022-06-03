package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.sensors.SensorId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class SensorIdReadingConverter : Converter<ObjectId, SensorId> {
    override fun convert(source: ObjectId): SensorId {
        return SensorId(source.toHexString())
    }
}

@Component
@WritingConverter
class SensorIdWritingConverter : Converter<SensorId, ObjectId> {
    override fun convert(source: SensorId): ObjectId {
        return ObjectId(source.value)
    }
}
