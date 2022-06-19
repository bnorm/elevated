package dev.bnorm.elevated.model.sensors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = SensorId.Serializer::class)
data class SensorId(
    val value: String,
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<SensorId> {
        override val descriptor = PrimitiveSerialDescriptor("sensorId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): SensorId {
            return SensorId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: SensorId) {
            encoder.encodeString(value.value)
        }
    }
}
