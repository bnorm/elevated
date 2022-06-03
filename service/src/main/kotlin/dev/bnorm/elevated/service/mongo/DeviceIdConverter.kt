package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.devices.DeviceId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class DeviceIdReadingConverter : Converter<ObjectId, DeviceId> {
    override fun convert(source: ObjectId): DeviceId {
        return DeviceId(source.toHexString())
    }
}

@Component
@WritingConverter
class DeviceIdWritingConverter : Converter<DeviceId, ObjectId> {
    override fun convert(source: DeviceId): ObjectId {
        return ObjectId(source.value)
    }
}
