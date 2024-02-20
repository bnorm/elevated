package dev.bnorm.elevated.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.StateFactoryMarker
import androidx.compose.runtime.structuralEqualityPolicy
import kotlinx.coroutines.CancellationException

sealed class NetworkResult<out T> {
    abstract val value: T

    abstract suspend fun <R> map(transform: suspend (T) -> R): NetworkResult<R>

    data object Loading : NetworkResult<Nothing>() {
        override val value: Nothing
            get() = throw RuntimeException("Loading")

        override suspend fun <R> map(transform: suspend (Nothing) -> R): Loading = this
    }

    class Error(val error: Throwable) : NetworkResult<Nothing>() {
        override val value: Nothing
            get() = throw error

        override suspend fun <R> map(transform: suspend (Nothing) -> R): Error = this
    }

    class Loaded<T>(override val value: T) : NetworkResult<T>() {
        override suspend fun <R> map(transform: suspend (T) -> R): NetworkResult<R> {
            return of { transform(value) }
        }
    }

    companion object {
        inline fun <T> of(supplier: () -> T): NetworkResult<T> {
            try {
                return Loaded(supplier())
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                return Error(t)
            }
        }

        @StateFactoryMarker
        fun <T> stateOf(
            policy: SnapshotMutationPolicy<NetworkResult<T>> = structuralEqualityPolicy(),
        ): MutableState<NetworkResult<T>> {
            return mutableStateOf(Loading, policy)
        }
    }
}

val <T : Any> NetworkResult<T>.valueOrNull: T?
    get() = when (this) {
        NetworkResult.Loading -> null
        is NetworkResult.Error -> null
        is NetworkResult.Loaded -> value
    }
