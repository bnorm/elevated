package dev.bnorm.elevated.model.devices

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DeviceId.Serializer::class)
class DeviceId(
    val value: String,
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<DeviceId> {
        override val descriptor = PrimitiveSerialDescriptor("deviceId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): DeviceId {
            return DeviceId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: DeviceId) {
            encoder.encodeString(value.value)
        }
    }
}
