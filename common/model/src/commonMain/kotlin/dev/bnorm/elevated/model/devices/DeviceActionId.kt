package dev.bnorm.elevated.model.devices

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DeviceActionId.Serializer::class)
data class DeviceActionId(
    val value: String,
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<DeviceActionId> {
        override val descriptor = PrimitiveSerialDescriptor("deviceActionId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): DeviceActionId {
            return DeviceActionId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: DeviceActionId) {
            encoder.encodeString(value.value)
        }
    }
}
