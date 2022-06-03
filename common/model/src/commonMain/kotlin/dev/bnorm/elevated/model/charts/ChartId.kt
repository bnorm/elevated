package dev.bnorm.elevated.model.charts

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ChartId.Serializer::class)
class ChartId(
    val value: String
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<ChartId> {
        override val descriptor = PrimitiveSerialDescriptor("chartId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): ChartId {
            return ChartId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: ChartId) {
            encoder.encodeString(value.value)
        }
    }
}
