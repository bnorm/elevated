package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.service.auth.AuthenticatedDevice
import dev.bnorm.elevated.service.auth.AuthorizationToken
import dev.bnorm.elevated.service.auth.JwtToken
import dev.bnorm.elevated.service.auth.encode
import dev.bnorm.elevated.service.auth.matches
import dev.bnorm.elevated.service.auth.toClaims
import dev.bnorm.elevated.service.devices.db.DeviceEntity
import dev.bnorm.elevated.service.devices.db.DeviceRepository
import dev.bnorm.elevated.service.sensors.SensorService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val sensorService: SensorService,
) {
    suspend fun authenticateDevice(request: DeviceLoginRequest): AuthenticatedDevice? {
        val device = deviceRepository.findById(request.id) ?: run {
            delay(500) // Fake work
            return null
        }
        return device.takeIf {
            passwordEncoder.matches(request.key, it.keyHash)
        }?.toDto()?.toAuthenticatedDevice()
    }

    suspend fun createDevice(prototype: DevicePrototype): Device {
        return deviceRepository.insert(prototype.toEntity()).toDto()
    }

    fun getAllDevices(): Flow<Device> {
        return deviceRepository.findAll().map { it.toDto() }
    }

    suspend fun getDeviceById(id: DeviceId): Device? {
        return deviceRepository.findById(id)?.toDto()
    }

    private suspend fun DeviceEntity.toDto(): Device {
        val deviceId = DeviceId(id)
        return Device(
            id = deviceId,
            name = name,
            sensors = sensorService.getSensorByDeviceId(deviceId).toList(),
        )
    }

    private fun DevicePrototype.toEntity(): DeviceEntity {
        return DeviceEntity(
            name = name,
            keyHash = passwordEncoder.encode(key)
        )
    }

    private fun Device.toAuthenticatedDevice(): AuthenticatedDevice {
        val claims = toClaims()
        val jwt = jwtEncoder.encode(claims)
        return AuthenticatedDevice(
            token = AuthorizationToken(
                type = "Bearer",
                value = JwtToken(jwt.tokenValue),
            ),
            device = this,
        )
    }
}
