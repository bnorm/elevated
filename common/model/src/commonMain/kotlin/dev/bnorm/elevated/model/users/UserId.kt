package dev.bnorm.elevated.model.users

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = UserId.Serializer::class)
class UserId(
    val value: String
) {
    override fun toString(): String = value

    companion object Serializer : KSerializer<UserId> {
        override val descriptor = PrimitiveSerialDescriptor("userId", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder): UserId {
            return UserId(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: UserId) {
            encoder.encodeString(value.value)
        }
    }
}
