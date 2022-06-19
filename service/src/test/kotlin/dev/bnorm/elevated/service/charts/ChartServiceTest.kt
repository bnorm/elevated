package dev.bnorm.elevated.service.charts

import dev.bnorm.elevated.model.charts.ChartCreateRequest
import dev.bnorm.elevated.service.charts.db.ChartEntity
import dev.bnorm.elevated.test.container.DockerContainerConfig
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection

@SpringBootTest
@Import(DockerContainerConfig::class)
class ChartServiceTest @Autowired constructor(
    private val chartService: ChartService,
    private val mongoOperations: ReactiveMongoOperations,
) {
    @AfterEach
    fun cleanup(): Unit = runBlocking {
        mongoOperations.dropCollection<ChartEntity>().awaitSingleOrNull()
    }

    @Test
    fun `can find chart`(): Unit = runBlocking {
        val expected = chartService.createChart(
            prototype = ChartCreateRequest(
                name = "Test Chart",
                targetPhLow = 1.0,
                targetPhHigh = 1.0,
                targetEcLow = 1.0,
                targetEcHigh = 1.0,
                microMl = 1.0,
                groMl = 1.0,
                bloomMl = 1.0,
            )
        )
        val actual = chartService.getChartById(expected.id)
        assertEquals(expected, actual)
    }
}
