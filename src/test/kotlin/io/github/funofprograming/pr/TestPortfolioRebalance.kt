package io.github.funofprograming.pr

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.funofprograming.pr.configuration.PortfolioConfiguration
import io.github.funofprograming.pr.configuration.PortfolioRebalanceExecutor
import io.github.funofprograming.pr.rule.PortfolioRuleExecutor
import io.github.funofprograming.pr.util.JsonMapperProvider
import io.github.funofprograming.pr.util.registerAllMarketValueCalculatorObjects
import io.github.funofprograming.pr.util.registerAllSecurityWeightCalculatorObjects
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class TestPortfolioRebalance {

    val prConfigTestFileName = "/portfolioConfig1.json"
    val prInputSecuritiesTestFileName = "/test_data_202503151239.csv"
    val jsonMapper:JsonMapper = JsonMapperProvider.getJsonMapper()
    var portfolioConfiguration:PortfolioConfiguration? = null
    var inputSecurities:DataFrame<*>? = null

    @BeforeEach
    fun loadConfigAndData():Unit {

        registerAllMarketValueCalculatorObjects()
        registerAllSecurityWeightCalculatorObjects()
        val prcIs = TestPortfolioRebalance::class.java.getResourceAsStream(prConfigTestFileName)
        val prcId = TestPortfolioRebalance::class.java.getResourceAsStream(prInputSecuritiesTestFileName)
        portfolioConfiguration = jsonMapper.readValue(prcIs)
        inputSecurities = DataFrame.readCSV(prcId)

    }

    @Test
    fun testPortfolioRebalance() {

        val portfolioRebalanceCommand = portfolioConfiguration?.let { PortfolioRebalanceCommand(it, LocalDate(2018,11,1)) }
        val prExecutor = portfolioRebalanceCommand?.let { PortfolioRebalanceExecutor(it) }
        prExecutor?.execute(inputSecurities ?: DataFrame.Empty)

    }
}