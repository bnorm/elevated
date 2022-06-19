package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.notifications.NotificationId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class NotificationIdReadingConverter : Converter<ObjectId, NotificationId> {
    override fun convert(source: ObjectId): NotificationId {
        return NotificationId(source.toHexString())
    }
}

@Component
@WritingConverter
class NotificationIdWritingConverter : Converter<NotificationId, ObjectId> {
    override fun convert(source: NotificationId): ObjectId {
        return ObjectId(source.value)
    }
}
