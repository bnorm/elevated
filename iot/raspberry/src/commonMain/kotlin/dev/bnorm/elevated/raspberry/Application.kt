package dev.bnorm.elevated.raspberry

expect fun createApplication(): Application

interface Application {
    suspend fun run()
}
