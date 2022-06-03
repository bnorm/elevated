package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.devices.DeviceActionId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class DeviceActionIdReadingConverter : Converter<ObjectId, DeviceActionId> {
    override fun convert(source: ObjectId): DeviceActionId {
        return DeviceActionId(source.toHexString())
    }
}

@Component
@WritingConverter
class DeviceActionIdWritingConverter : Converter<DeviceActionId, ObjectId> {
    override fun convert(source: DeviceActionId): ObjectId {
        return ObjectId(source.value)
    }
}
