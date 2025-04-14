package io.github.funofprograming.pr.util

import DEFAULT_PRECISION
import MARKET_VALUE_CALCULATOR_REGISTRY
import REBAL_LOOP_RULE_STATES_KEY
import REGISTERED_RULE_REGISTRY
import SECURITY_WEIGHT_CALCULATOR_REGISTRY
import SECURITY_WEIGHT_CAPPER_REGISTRY
import SECURITY_WEIGHT_CAPPING_STRATEGY_REGISTRY
import io.github.funofprograming.context.ApplicationContext
import io.github.funofprograming.context.Key
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.Attribute
import io.github.funofprograming.pr.rule.AttributeType
import io.github.funofprograming.pr.rule.RegistrableRule
import io.github.funofprograming.pr.rule.mv.EquitiesMarketValueCalculator
import io.github.funofprograming.pr.rule.mv.SecurityMarketValueCalculator
import io.github.funofprograming.pr.rule.weight.MarketValueSecurityWeightCalculator
import io.github.funofprograming.pr.rule.weight.SecurityWeightCalculator
import io.github.funofprograming.pr.rule.weight.capping.*
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.update
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.w3c.dom.Attr
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

inline fun getGlobalRebalanceContext(rebalanceId: UUID):ApplicationContext? = getGlobalContext(rebalanceId.toString())

fun isLoopContinueNextIteration(rebalanceId: UUID, loopLabel: String?):Boolean {

    val rebalanceContext = getGlobalRebalanceContext(rebalanceId)
    val loopState = rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()
    return loopState?.loopLabel == loopLabel && loopState?.continueNextIteration?.get() ?: false
}

fun isLoopInnermost(rebalanceId: UUID, loopLabel: String?):Boolean {
    val rebalanceContext = getGlobalRebalanceContext(rebalanceId)
    val loopState = rebalanceContext?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()
    return loopState?.loopLabel == loopLabel
}

inline fun isInnermostLoopIterationExhausted(rebalanceId: UUID): Boolean =
    Optional.ofNullable(getGlobalRebalanceContext(rebalanceId)?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.peek()).map { ls->ls.maxIterations == ls.currentIteration.get() }.orElse(false)

inline fun isInsideLoop(rebalanceId: UUID): Boolean = getGlobalRebalanceContext(rebalanceId)?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.isNotEmpty() ?: false

inline fun breakLook(rebalanceId: UUID):Unit {
    if(isInsideLoop(rebalanceId))
        getGlobalRebalanceContext(rebalanceId)?.fetch(REBAL_LOOP_RULE_STATES_KEY)?.pop()
    return
}

fun <T> getObject(attr: Attribute<T>?, row: DataRow<*>): T? {

    val name = attr?.name
    val type = attr?.type
    val literal = attr?.literalValue

    if(literal != null) {
        return  literal

    } else if(name != null) {
        val accessor = getAccessor(name, type)
        return row.getValueOrNull((accessor as ColumnReference<*>)) as T?
    }

    throw IllegalArgumentException("Invalid attribute: $attr")
}

fun getAccessor(colName: String?, type: AttributeType?): ColumnAccessor<*>? {

    if (colName == null || type == null)
        return null

    return when(type) {
        AttributeType.STRING -> column<java.lang.String>(colName)
        AttributeType.NUMBER -> column<java.math.BigDecimal>(colName)
        AttributeType.LOCAL_DATE -> column<java.time.LocalDate>(colName)
        AttributeType.LOCAL_DATE_TIME -> column<java.time.LocalDateTime>(colName)
        AttributeType.ZONED_DATE_TIME -> column<java.time.ZonedDateTime>(colName)
        AttributeType.INSTANT -> column<java.time.Instant>(colName)
    }
}

fun registerAllSecurityWeightCalculatorObjects() {
    registerSecurityWeightCalculator(MarketValueSecurityWeightCalculator)
}

fun registerAllMarketValueCalculatorObjects() {
    registerMarketValueCalculator(EquitiesMarketValueCalculator)
}

fun registerAllSecurityWeightCapperObjects() {
    registerSecurityWeightCapper(EquityPortfolioAmountLimitSecurityWeightCapper)
    registerSecurityWeightCapper(MarketValueSecurityWeightCapper)
}

fun registerAllSecurityWeightCappingStrategyObjects() {
    registerSecurityWeightCappingStrategy(ProRataSecurityWeightCappingStrategy)
}

fun PortfolioRebalanceCommand.registerSecurityWeightCalculator(securityWeightCalculator: SecurityWeightCalculator) = io.github.funofprograming.pr.util.registerSecurityWeightCalculator(securityWeightCalculator)
fun PortfolioRebalanceCommand.deregisterSecurityWeightCalculator(securityWeightCalculatorId: String) = io.github.funofprograming.pr.util.deregisterSecurityWeightCalculator(securityWeightCalculatorId)
fun PortfolioRebalanceCommand.registerSecurityWeightCapper(securityWeightCapper: SecurityWeightCapper) = io.github.funofprograming.pr.util.registerSecurityWeightCapper(securityWeightCapper)
fun PortfolioRebalanceCommand.deregisterSecurityWeightCapper(securityWeightCapperId: String) = io.github.funofprograming.pr.util.deregisterSecurityWeightCapper(securityWeightCapperId)
fun PortfolioRebalanceCommand.registerMarketValueCalculator(marketValueCalculator: SecurityMarketValueCalculator) = io.github.funofprograming.pr.util.registerMarketValueCalculator(marketValueCalculator)
fun PortfolioRebalanceCommand.deregisterMarketValueCalculator(marketValueCalculatorId: String) = io.github.funofprograming.pr.util.deregisterMarketValueCalculator(marketValueCalculatorId)
fun PortfolioRebalanceCommand.registerPortfolioRule(registrableRule: RegistrableRule) = io.github.funofprograming.pr.util.registerPortfolioRule(registrableRule)
fun PortfolioRebalanceCommand.deregisterPortfolioRule(registrableRuleId: String) = io.github.funofprograming.pr.util.deregisterPortfolioRule(registrableRuleId)

fun registerSecurityWeightCalculator(securityWeightCalculator: SecurityWeightCalculator) =
    SECURITY_WEIGHT_CALCULATOR_REGISTRY?.add(Key.of<SecurityWeightCalculator>(securityWeightCalculator.securityWeightCalculatorId()), securityWeightCalculator)

fun deregisterSecurityWeightCalculator(securityWeightCalculatorId: String) =
    SECURITY_WEIGHT_CALCULATOR_REGISTRY?.erase(Key.of<SecurityWeightCalculator>(securityWeightCalculatorId))

fun registerMarketValueCalculator(marketValueCalculator: SecurityMarketValueCalculator) =
    MARKET_VALUE_CALCULATOR_REGISTRY?.add(Key.of<SecurityMarketValueCalculator>(marketValueCalculator.securityMarketValueCalculatorId()), marketValueCalculator)

fun deregisterMarketValueCalculator(marketValueCalculatorId: String) =
    MARKET_VALUE_CALCULATOR_REGISTRY?.erase(Key.of<SecurityMarketValueCalculator>(marketValueCalculatorId))

fun registerSecurityWeightCapper(securityWeightCapper: SecurityWeightCapper) =
    SECURITY_WEIGHT_CAPPER_REGISTRY?.add(Key.of<SecurityWeightCapper>(securityWeightCapper.securityWeightCapperId()), securityWeightCapper)

fun deregisterSecurityWeightCapper(securityWeightCapperId: String) =
    SECURITY_WEIGHT_CAPPER_REGISTRY?.erase(Key.of<SecurityWeightCapper>(securityWeightCapperId))

fun registerSecurityWeightCappingStrategy(securityWeightCappingStrategy: SecurityWeightCappingStrategy) =
    SECURITY_WEIGHT_CAPPING_STRATEGY_REGISTRY?.add(Key.of<SecurityWeightCappingStrategy>(securityWeightCappingStrategy.securityWeightCappingStrategyId()), securityWeightCappingStrategy)

fun deregisterSecurityWeightCappingStrategy(securityWeightCappingStrategyId: String) =
    SECURITY_WEIGHT_CAPPING_STRATEGY_REGISTRY?.erase(Key.of<SecurityWeightCappingStrategy>(securityWeightCappingStrategyId))

fun registerPortfolioRule(registrableRule: RegistrableRule) =
    REGISTERED_RULE_REGISTRY?.add(Key.of<RegistrableRule>(registrableRule.registerableRuleId()), registrableRule)

fun deregisterPortfolioRule(registerableRuleId: String) =
    REGISTERED_RULE_REGISTRY?.erase(Key.of<RegistrableRule>(registerableRuleId))

fun getSecurityWeightCalculator(securityWeightCalculatorId: String): SecurityWeightCalculator? = SECURITY_WEIGHT_CALCULATOR_REGISTRY?.fetch(Key.of<SecurityWeightCalculator>(securityWeightCalculatorId))

fun getMarketValueCalculator(marketValueCalculatorId: String): SecurityMarketValueCalculator? = MARKET_VALUE_CALCULATOR_REGISTRY?.fetch(Key.of<SecurityMarketValueCalculator>(marketValueCalculatorId))

fun getSecurityWeightCapper(securityWeightCapperId: String): SecurityWeightCapper? = SECURITY_WEIGHT_CAPPER_REGISTRY?.fetch(Key.of<SecurityWeightCapper>(securityWeightCapperId))

fun getSecurityWeightCappingStrategy(securityWeightCappingStrategyId: String): SecurityWeightCappingStrategy? = SECURITY_WEIGHT_CAPPING_STRATEGY_REGISTRY?.fetch(Key.of<SecurityWeightCappingStrategy>(securityWeightCappingStrategyId))

fun getRegisteredPortfolioRule(registerableRuleId: String): RegistrableRule? = REGISTERED_RULE_REGISTRY?.fetch(Key.of<RegistrableRule>(registerableRuleId))

inline fun BigDecimal.safeDivide(divisor: BigDecimal):BigDecimal = if(divisor == BigDecimal.ZERO) BigDecimal.ZERO else this.divide(divisor, DEFAULT_PRECISION)

inline fun BigDecimal.safeDivide(divisor: BigDecimal, precision: MathContext):BigDecimal = if(divisor == BigDecimal.ZERO) BigDecimal.ZERO else this.divide(divisor, precision)

inline fun <reified T> DataFrame<*>.addOrUpdateColumnInDataFrame(column: ColumnAccessor<T>, crossinline expression: (DataRow<*>, T?) -> T):DataFrame<*>? {

    var dataframeResult =
        if(this.getColumnOrNull(column) == null)
            this.add(column) { row->expression.invoke(row, null) }
        else
            this.update(column)?.perRowCol {row, col -> expression.invoke(row, row.getValueOrNull(col))}

    return dataframeResult
}

