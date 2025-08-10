package dev.bnorm.elevated.model

import dev.bnorm.elevated.model.Optional.Empty
import kotlin.jvm.JvmInline
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@JvmInline
@Serializable(with = Optional.Serializer::class)
value class Optional<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) {
    val isEmpty: Boolean get() = value === Empty

    override fun toString(): String =
        when (value) {
            Empty -> "EMPTY"
            else -> "OF($value)"
        }

    @PublishedApi
    internal data object Empty

    companion object {
        private val EMPTY = Optional<Any?>(Empty)

        fun <T> of(value: T): Optional<T> = Optional(value)
        fun <T> empty(): Optional<T> {
            @Suppress("UNCHECKED_CAST")
            return EMPTY as Optional<T>
        }
    }

    @PublishedApi
    internal class Serializer<T>(
        private val element: KSerializer<T>,
    ) : KSerializer<Optional<T>> {
        @OptIn(InternalSerializationApi::class)
        override val descriptor = SerialDescriptor("dev.bnorm.elevated.model.Optional", element.descriptor)

        override fun serialize(encoder: Encoder, value: Optional<T>) {
            value.ifPresent { encoder.encodeSerializableValue(element, it) }
        }

        override fun deserialize(decoder: Decoder): Optional<T> {
            return Optional.of(decoder.decodeSerializableValue(element))
        }
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <T> Optional<T>.ifPresent(block: (T) -> Unit) {
    if (value !== Empty) block(value as T)
}

@Suppress("UNCHECKED_CAST")
inline fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> {
    return if (value !== Empty) Optional.of(transform(value as T)) else Optional.empty()
}

@Suppress("UNCHECKED_CAST")
inline fun <T> Optional<T>.getOrElse(onEmpty: () -> T): T {
    return if (value !== Empty) value as T else onEmpty()
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Optional<T>.getOrNull(): T? {
    return if (value !== Empty) value as T else null
}
