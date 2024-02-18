package dev.bnorm.elevated.raspberry

import dev.bnorm.Gpio
import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.pumps.PumpId
import dev.bnorm.gpio.Output
import dev.bnorm.gpio.PinState
import kotlinx.coroutines.delay

fun PumpService(): PumpService {
    // Hardcode known pumps
    return PumpService(
        all = PumpType.entries.map { Pump(it.id, it.address, it.rate) }
    )
}

fun Pump(
    id: PumpId,
    address: Int,
    rate: Double // ml / second
): Pump {
    val name = "pump" + id.toString().padStart(2, '0')
    val output = Gpio().output(address, defaultState = PinState.HIGH)
    return GpioPump(output, name, rate, id, Pump.State.OFF)
}

class GpioPump(
    private val output: Output,
    private val name: String,
    private val rate: Double, // ml / second
    override val id: PumpId,
    override var state: Pump.State,
) : Pump {
    companion object {
        private val log = getLogger<Pump>()
    }

    override fun on() {
        log.trace { "Enter method=Pump::on id=$id" }
        output.setState(PinState.LOW)
        state = Pump.State.ON
        log.trace { "Exit method=Pump::on id=$id" }
    }

    override fun off() {
        log.trace { "Enter method=Pump::off id=$id" }
        output.setState(PinState.HIGH)
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
