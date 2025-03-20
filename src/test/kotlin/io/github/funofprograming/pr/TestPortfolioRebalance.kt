package io.github.funofprograming.pr

import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestPortfolioRebalance {

    val prConfigTestFileName = "/portfolioConfig1.json"
    val prInputSecuritiesTestFileName = "/test_data_input_securities_202503151239.csv"
    val prLastRebalanceConstituentsTestFileName = "/test_data_last_rebal_const_202503201034.csv"

    @BeforeEach
    fun loadConfigAndData():Unit {


    }

    @Test
    fun testPortfolioRebalance() {

        val prcIs = TestPortfolioRebalance::class.java.getResourceAsStream(prConfigTestFileName)
        val prcId = TestPortfolioRebalance::class.java.getResourceAsStream(prInputSecuritiesTestFileName)
        val prcLr = TestPortfolioRebalance::class.java.getResourceAsStream(prLastRebalanceConstituentsTestFileName)
        val prCmd =
            PortfolioRebalanceCommand.builder()
                .setPortfolioConfigurationJson(prcIs)
                .setRebalanceDate(LocalDate(2018,11,1))
                .setInputSecuritiesCsv(prcId)
                .setLastRebalanceConstituentsCsv(prcLr)
                .build()
        val prResult = prCmd.execute()
        println(prResult)

    }
}