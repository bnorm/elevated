package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.log.getLogger
import dev.bnorm.elevated.model.pumps.PumpId
import kotlinx.coroutines.delay

interface Pump {
    val id: PumpId
    val state: State

    fun on()
    fun off()

    suspend fun dispense(milliliters: Double)

    enum class State {
        ON,
        OFF,
    }
}

class FakePump(
    override val id: PumpId,
    private val rate: Double, // ml / second
) : Pump {
    companion object {
        private val log = getLogger<Pump>()
    }

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
