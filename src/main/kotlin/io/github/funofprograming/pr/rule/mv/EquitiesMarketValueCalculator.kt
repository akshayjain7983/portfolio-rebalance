package io.github.funofprograming.pr.rule.mv

import io.github.funofprograming.pr.util.addOrUpdateColumnInDataFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.getValue
import java.math.BigDecimal
import java.util.*

object EquitiesMarketValueCalculator: SecurityMarketValueCalculator {

    override fun securityMarketValueCalculatorId(): String = "EquitiesMarketValueCalculator"

    override fun setupMarketValues(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>? {

        var securitiesResult = securities
        val rbPrice by column<BigDecimal>("rebalance_price")
        val closePrice by column<Number>("close_price")
        val rbUnits by column<Long>("rebalance_units")
        val units by column<Number>("units")
        val marketValue by column<BigDecimal>("market_value")
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(rbPrice) { row, rbPriceVal -> rbPriceVal ?: BigDecimal.valueOf(row[closePrice].toDouble()) }
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(rbUnits) { row, rbUnitsVal -> rbUnitsVal ?: row[units].toLong() }
        securitiesResult = securitiesResult?.addOrUpdateColumnInDataFrame(marketValue) { row, _ -> row[rbPrice].multiply(BigDecimal.valueOf(row[rbUnits])) }
        return securitiesResult
    }
}