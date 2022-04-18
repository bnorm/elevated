package dev.bnorm.elevated.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ElevatedApplication

fun main(args: Array<String>) {
    runApplication<ElevatedApplication>(*args)
}
