package dev.bnorm.elevated.raspberry

import dev.bnorm.elevated.model.pumps.PumpId

class PumpService(
    val all: List<Pump>
) {
    private val map: Map<PumpId, Pump> = all.associateBy { it.id }

    operator fun get(id: PumpId): Pump? = map[id]
    operator fun get(type: PumpType): Pump = map.getValue(type.id)
}

enum class PumpType(
    val id: PumpId,
    val address: Int,
    val rate: Double // ml / second
) {
    PhDown(PumpId("65d285fc9c4278469bfb17a7"), address = 17, rate = 1.2540),
    Micro(PumpId("65d2862b9c4278469bfb17aa"), address = 18, rate = 1.2540),
    Gro(PumpId("65d286309c4278469bfb17ab"), address = 27, rate = 1.1989),
    Bloom(PumpId("65d286349c4278469bfb17ac"), address = 22, rate = 1.1989),
}

fun FakePumpService(): PumpService {
    // Hardcode known pumps
    return PumpService(
        all = PumpType.entries.map { FakePump(it.id, it.rate) }
    )
}
