package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.users.UserId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class UserIdReadingConverter : Converter<ObjectId, UserId> {
    override fun convert(source: ObjectId): UserId {
        return UserId(source.toHexString())
    }
}

@Component
@WritingConverter
class UserIdWritingConverter : Converter<UserId, ObjectId> {
    override fun convert(source: UserId): ObjectId {
        return ObjectId(source.value)
    }
}
