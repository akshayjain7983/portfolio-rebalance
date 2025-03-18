package io.github.funofprograming.pr.rule.mv

import org.jetbrains.kotlinx.dataframe.DataFrame
import java.util.*

interface SecurityMarketValueCalculator {

    fun securityMarketValueCalculatorId(): String

    fun setupMarketValues(rebalanceId: UUID, securities: DataFrame<*>?): DataFrame<*>?
}