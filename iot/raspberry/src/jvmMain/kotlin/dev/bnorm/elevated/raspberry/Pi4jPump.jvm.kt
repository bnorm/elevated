package dev.bnorm.elevated.raspberry

import com.pi4j.context.Context
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalState
import dev.bnorm.elevated.log.getLogger
import kotlinx.coroutines.delay

fun Context.PumpService(): PumpService {
    // Hardcode known pumps
    return PumpService(
        all = PumpType.values().map { Pump(it.id, it.address, it.rate) }
    )
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

private class Pi4jPump(
    private val output: DigitalOutput,
    private val name: String,
    private val rate: Double, // ml / second
    override val id: Int,
    override var state: Pump.State,
) : Pump {
    companion object {
        private val log = getLogger<Pump>()
    }

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
