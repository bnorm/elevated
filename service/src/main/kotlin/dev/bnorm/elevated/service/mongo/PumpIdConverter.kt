package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.pumps.PumpId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class PumpIdReadingConverter : Converter<ObjectId, PumpId> {
    override fun convert(source: ObjectId): PumpId {
        return PumpId(source.toHexString())
    }
}

@Component
@WritingConverter
class PumpIdWritingConverter : Converter<PumpId, ObjectId> {
    override fun convert(source: PumpId): ObjectId {
        return ObjectId(source.value)
    }
}
