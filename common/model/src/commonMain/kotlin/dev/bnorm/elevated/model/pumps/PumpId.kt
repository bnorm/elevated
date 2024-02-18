package dev.bnorm.elevated.model.pumps

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = PumpId.Serializer::class)
data class PumpId(
    val value: String,
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<PumpId> {
        override val descriptor = PrimitiveSerialDescriptor("pumpId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): PumpId {
            return PumpId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: PumpId) {
            encoder.encodeString(value.value)
        }
    }
}
