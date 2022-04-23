package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtToken
import dev.bnorm.elevated.model.devices.Device
import dev.bnorm.elevated.model.devices.DeviceId
import dev.bnorm.elevated.model.devices.DeviceLoginRequest
import dev.bnorm.elevated.model.devices.DevicePrototype
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
import kotlinx.datetime.toKotlinInstant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.stereotype.Service
import java.time.Instant

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

    suspend fun deleteDevice(deviceId: DeviceId) {
        deviceRepository.delete(deviceId)
    }

    fun getAllDevices(): Flow<Device> {
        return deviceRepository.findAll().map { it.toDto() }
    }

    suspend fun getDeviceById(deviceId: DeviceId): Device? {
        return deviceRepository.findById(deviceId)?.toDto()
    }

    suspend fun updateDevice(deviceId: DeviceId, timestamp: Instant): Device? {
        return deviceRepository.modify(deviceId, timestamp)?.toDto()
    }

    private suspend fun DeviceEntity.toDto(): Device {
        val deviceId = DeviceId(id)
        return Device(
            id = deviceId,
            name = name,
            sensors = sensorService.getSensorByDeviceId(deviceId).toList(),
            lastActionTime = lastActionTime?.toKotlinInstant(),
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
