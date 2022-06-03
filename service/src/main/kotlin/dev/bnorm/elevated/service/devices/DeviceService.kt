package dev.bnorm.elevated.service.devices

import dev.bnorm.elevated.model.auth.AuthenticatedDevice
import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtToken
import dev.bnorm.elevated.model.charts.ChartId
import dev.bnorm.elevated.model.devices.*
import dev.bnorm.elevated.service.auth.encode
import dev.bnorm.elevated.service.auth.matches
import dev.bnorm.elevated.service.auth.toClaims
import dev.bnorm.elevated.service.charts.ChartService
import dev.bnorm.elevated.service.devices.db.DeviceActionRepository
import dev.bnorm.elevated.service.devices.db.DeviceEntity
import dev.bnorm.elevated.service.devices.db.DeviceRepository
import dev.bnorm.elevated.service.devices.db.DeviceUpdate
import dev.bnorm.elevated.service.sensors.SensorService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    private val deviceActionRepository: DeviceActionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtEncoder: JwtEncoder,
    private val sensorService: SensorService,
    private val chartService: ChartService,
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

    suspend fun createDevice(prototype: DeviceCreateRequest): Device {
        return deviceRepository.insert(prototype.toEntity()).toDto()
    }

    suspend fun deleteDevice(deviceId: DeviceId) {
        deviceRepository.delete(deviceId)
        deviceActionRepository.deleteByDeviceId(deviceId)
    }

    fun getAllDevices(): Flow<Device> {
        return deviceRepository.findAll().map { it.toDto() }
    }

    suspend fun getDeviceById(deviceId: DeviceId): Device? {
        return deviceRepository.findById(deviceId)?.toDto()
    }

    suspend fun patchDeviceById(deviceId: DeviceId, request: DevicePatchRequest): Device? {
        return deviceRepository.modify(deviceId, request.toUpdate())?.toDto()
    }

    suspend fun updateDevice(deviceId: DeviceId, timestamp: Instant): Device? {
        return deviceRepository.modify(deviceId, DeviceUpdate(lastActionTime = timestamp))?.toDto()
    }

    suspend fun setDeviceStatus(deviceId: DeviceId, status: DeviceStatus): Device? {
        return deviceRepository.modify(deviceId, DeviceUpdate(status = status))?.toDto()
    }

    private suspend fun DeviceEntity.toDto(): Device = coroutineScope {
        val sensors = async { sensorService.getSensorsByDeviceId(id).toList() }
        val chart = async { chartId?.let { chartService.getChartById(ChartId(it)) } }
        return@coroutineScope Device(
            id = id,
            name = name,
            status = status,
            sensors = sensors.await(),
            lastActionTime = lastActionTime?.toKotlinInstant(),
            chart = chart.await()
        )
    }

    private fun DeviceCreateRequest.toEntity(): DeviceEntity {
        return DeviceEntity(
            name = name,
            keyHash = passwordEncoder.encode(key)
        )
    }

    private fun DevicePatchRequest.toUpdate(): DeviceUpdate {
        return DeviceUpdate(
            name = name,
            keyHash = key?.let { passwordEncoder.encode(it) },
            status = null,
            lastActionTime = null,
            chartId = chartId,
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
