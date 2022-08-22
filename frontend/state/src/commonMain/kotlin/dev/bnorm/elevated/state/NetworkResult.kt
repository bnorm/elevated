package dev.bnorm.elevated.state

sealed class NetworkResult<out T> {
    abstract val value: T

    object Loading : NetworkResult<Nothing>() {
        override val value: Nothing
            get() = throw RuntimeException("Loading")
    }

    class Error(val error: Throwable) : NetworkResult<Nothing>() {
        override val value: Nothing
            get() = throw error
    }

    class Loaded<T>(override val value: T) : NetworkResult<T>()
}
