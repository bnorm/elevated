package dev.bnorm.elevated.service.charts

import dev.bnorm.elevated.model.charts.Chart
import dev.bnorm.elevated.model.charts.ChartCreateRequest
import dev.bnorm.elevated.model.pumps.PumpContent
import dev.bnorm.elevated.model.sensors.MeasurementType
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
                bounds = listOf(
                    Chart.Bound(
                        type = MeasurementType.PH,
                        low = 1.0,
                        high = 1.0,
                    ),
                    Chart.Bound(
                        type = MeasurementType.EC,
                        low = 1.0,
                        high = 1.0,
                    ),
                ),
                amounts = mapOf(
                    PumpContent.GENERAL_HYDROPONICS_MICRO to 1.0,
                    PumpContent.GENERAL_HYDROPONICS_GRO to 1.0,
                    PumpContent.GENERAL_HYDROPONICS_BLOOM to 1.0,
                )
            )
        )
        val actual = chartService.getChartById(expected.id)
        assertEquals(expected, actual)
    }
}
