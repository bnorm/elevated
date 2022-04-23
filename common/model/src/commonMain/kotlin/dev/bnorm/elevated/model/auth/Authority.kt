package dev.bnorm.elevated.model.auth

enum class Authority(
    vararg inherits: Authority
) {
    CHARTS_READ,
    CHARTS_WRITE(CHARTS_READ),
    CHARTS_ADMIN(CHARTS_WRITE),

    DEVICES_READ,
    DEVICES_WRITE(DEVICES_READ),
    DEVICES_ADMIN(DEVICES_WRITE),

    READINGS_READ,
    READINGS_WRITE(READINGS_READ),
    READINGS_ADMIN(READINGS_WRITE),

    SENSORS_READ,
    SENSORS_WRITE(SENSORS_READ),
    SENSORS_ADMIN(SENSORS_WRITE),

    USERS_READ,
    USERS_WRITE(USERS_READ),
    USERS_ADMIN(USERS_WRITE),

    ;

    val inherits: Set<Authority> = setOf(*inherits)
}
