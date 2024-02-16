package dev.bnorm.elevated.raspberry

class PumpService(
    val all: List<Pump>
) {
    private val map: Map<Int, Pump> = all.associateBy { it.id }

    operator fun get(id: Int): Pump? = map[id]
    operator fun get(type: PumpType): Pump = map.getValue(type.id)
}

enum class PumpType(
    val id: Int,
    val address: Int,
    val rate: Double // ml / second
) {
    PhDown(1, address = 17, rate = 1.2540),
    Micro(2, address = 18, rate = 1.2540),
    Gro(3, address = 27, rate = 1.1989),
    Bloom(4, address = 22, rate = 1.1989),
}

fun FakePumpService(): PumpService {
    // Hardcode known pumps
    return PumpService(
        all = PumpType.values().map { FakePump(it.id, it.rate) }
    )
}
