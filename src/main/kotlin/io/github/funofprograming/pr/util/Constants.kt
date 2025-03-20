import io.github.funofprograming.context.Key
import io.github.funofprograming.context.impl.getGlobalContext
import io.github.funofprograming.pr.rule.RelaxationCondition
import io.github.funofprograming.pr.rule.loop.LoopState
import io.github.funofprograming.pr.vo.PortfolioRebalance
import io.github.funofprograming.pr.vo.PortfolioRebalanceCommand
import java.math.BigDecimal
import java.math.MathContext
import java.util.concurrent.BlockingDeque


val DEFAULT_PRECISION:MathContext = MathContext.DECIMAL128
val REBAL_CMD_KEY = Key.of<PortfolioRebalanceCommand>("PortfolioRebalanceCommand")
val REBAL_OUTPUT_KEY = Key.of<PortfolioRebalance>("RebalanceOutput")
val REBAL_LOOP_RULE_STATES_KEY = Key.of<BlockingDeque<LoopState>>("LoopRuleStates")
val REBAL_RELAXATION_COND_KEY = Key.of<RelaxationCondition>("RebalanceRelaxationCondition")
val PORTFOLIO_SIZE_CURRENT = Key.of<BigDecimal>("PortfolioSizeCurrent")
val SECURITY_WEIGHT_CALCULATOR_REGISTRY = getGlobalContext("SECURITY_WEIGHT_CALCULATOR_REGISTRY")
val MARKET_VALUE_CALCULATOR_REGISTRY = getGlobalContext("MARKET_VALUE_CALCULATOR_REGISTRY")
val REGISTERED_RULE_REGISTRY = getGlobalContext("REGISTERED_RULE_REGISTRY")
