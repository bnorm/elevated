package dev.bnorm.elevated.model.notifications

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NotificationId.Serializer::class)
data class NotificationId(
    val value: String,
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<NotificationId> {
        override val descriptor = PrimitiveSerialDescriptor("notificationId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): NotificationId {
            return NotificationId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: NotificationId) {
            encoder.encodeString(value.value)
        }
    }
}
