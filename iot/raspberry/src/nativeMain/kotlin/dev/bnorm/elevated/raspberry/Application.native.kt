package dev.bnorm.elevated.raspberry

actual fun createApplication(): Application {
    return NativeApplication()
}

private class NativeApplication : Application {
    override suspend fun run() {
        throw UnsupportedOperationException("Not Implemented for native platforms!")
    }
}
