package dev.bnorm.elevated.service.mongo

import dev.bnorm.elevated.model.charts.ChartId
import org.bson.types.ObjectId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

@Component
@ReadingConverter
class ChartIdReadingConverter : Converter<ObjectId, ChartId> {
    override fun convert(source: ObjectId): ChartId {
        return ChartId(source.toHexString())
    }
}

@Component
@WritingConverter
class ChartIdWritingConverter : Converter<ChartId, ObjectId> {
    override fun convert(source: ChartId): ObjectId {
        return ObjectId(source.value)
    }
}
