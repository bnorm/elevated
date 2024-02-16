package dev.bnorm.elevated.raspberry

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import dev.bnorm.elevated.log.getLogger
import kotlinx.coroutines.delay

interface Pump {
    val id: Int
    val state: State

    fun on()
    fun off()

    suspend fun dispense(milliliters: Double)

    enum class State {
        ON,
        OFF,
    }
}

fun Context.Pump(
    id: Int,
    address: Int,
    rate: Double // ml / second
): Pump {
    val name = "pump" + id.toString().padStart(2, '0')
    val config = DigitalOutput.newConfigBuilder(this)
        .address(address)
        .id(name)
        .name(name)
        .shutdown(DigitalState.HIGH)
        .initial(DigitalState.HIGH)
        .provider("pigpio-digital-output")
    val output = create(config)
    return Pi4jPump(output, name, rate, id, Pump.State.OFF)
}

private val log = getLogger<Pump>()

private class Pi4jPump(
    private val output: DigitalOutput,
    private val name: String,
    private val rate: Double, // ml / second
    override val id: Int,
    override var state: Pump.State,
) : Pump {
    override fun on() {
        log.trace { "Enter method=Pump::on id=$id" }
        output.low()
        state = Pump.State.ON
        log.trace { "Exit method=Pump::on id=$id" }
    }

    override fun off() {
        log.trace { "Enter method=Pump::off id=$id" }
        output.high()
        state = Pump.State.OFF
        log.trace { "Exit method=Pump::off id=$id" }
    }

    override suspend fun dispense(milliliters: Double) {
        log.trace { "Enter method=Pump::dispense milliliters=$milliliters id=$id" }
        val milliseconds = milliliters / rate * 1000
        try {
            on()
            delay(milliseconds.toLong())
        } finally {
            off() // always attempt to turn off
            log.trace { "Exit method=Pump::dispense milliliters=$milliliters id=$id" }
        }
    }

    override fun toString(): String = name
}

class FakePump(
    override val id: Int,
    private val rate: Double, // ml / second
) : Pump {
    private val name: String = "fake_pump" + id.toString().padStart(2, '0')
    override var state: Pump.State = Pump.State.OFF

    override fun on() {
        log.trace { "Enter method=Pump::on id=$id" }
        state = Pump.State.ON
        log.trace { "Exit method=Pump::on id=$id" }
    }

    override fun off() {
        log.trace { "Enter method=Pump::off id=$id" }
        state = Pump.State.OFF
        log.trace { "Exit method=Pump::off id=$id" }
    }

    override suspend fun dispense(milliliters: Double) {
        log.trace { "Enter method=Pump::dispense milliliters=$milliliters id=$id" }
        val milliseconds = milliliters / rate * 1000
        try {
            on()
            delay(milliseconds.toLong())
        } finally {
            off() // always attempt to turn off
            log.trace { "Exit method=Pump::dispense milliliters=$milliliters id=$id" }
        }
    }

    override fun toString(): String = name
}
